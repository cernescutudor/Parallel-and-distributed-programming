using System;
using System.Net.Sockets;
using System.Text;
// Write a program that is capable of simultaneously downloading several files through HTTP. Use directly the BeginConnect()/EndConnect(), BeginSend()/EndSend() and BeginReceive()/EndReceive() Socket functions, and write a simple parser for the HTTP protocol (it should be able only to get the header lines and to understand the Content-lenght: header line).


// Try three implementations:

// Directly implement the parser on the callbacks (event-driven);
// Wrap the connect/send/receive operations in tasks, with the callback setting the result of the task;
//Like the previous, but also use the async/await mechanism.

namespace ConsoleApp
{
    public class EventDrivenDownloader
    {
        private readonly string _host;
        private readonly string _path;
        private readonly Socket _socket;
        private readonly byte[] _buffer = new byte[1024];
        private int _contentLength = -1; // in bytes 
        private int _receivedLength = 0; // in bytes
        private bool _headersComplete = false; // if the parsing of the headers is complete
        private StringBuilder _headers = new StringBuilder();

        public EventDrivenDownloader(string host, string path)
        {
            _host = host;
            _path = path;
            _socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        }

        public void Start()
        {
            Console.WriteLine("Starting connection...");
            _socket.BeginConnect(_host, 80, OnConnect, null);
        }

        private byte[] CreateGetRequest()
        {
            string request = $"GET {_path} HTTP/1.1\r\n" +
                             $"Host: {_host}\r\n" +
                             "Connection: close\r\n\r\n";
            return Encoding.ASCII.GetBytes(request);
        }

        private void OnConnect(IAsyncResult ar)
        {
            _socket.EndConnect(ar); // complete the connection
            Console.WriteLine("Connected to server.");
            byte[] request = CreateGetRequest();
            _socket.BeginSend(request, 0, request.Length, SocketFlags.None, OnSend, null);
        }

        private void OnSend(IAsyncResult ar)
        {
            _socket.EndSend(ar); // complete the send
            Console.WriteLine("Request sent.");
            _socket.BeginReceive(_buffer, 0, _buffer.Length, SocketFlags.None, OnReceive, null);
        }

        private void OnReceive(IAsyncResult ar)
        {
            int bytesReceived = _socket.EndReceive(ar); // complete the receive
            if (bytesReceived > 0)
            {
                string responsePart = Encoding.ASCII.GetString(_buffer, 0, bytesReceived);
                if (!_headersComplete)
                {
                    _headers.Append(responsePart);

                    // check if the headers are complete
                    int headersEndIndex = _headers.ToString().IndexOf("\r\n\r\n", StringComparison.Ordinal);

                    if (headersEndIndex != -1)
                    {
                        _headersComplete = true;
                        Console.WriteLine("Headers complete");
                        ParseHeaders(_headers.ToString().Substring(0, headersEndIndex));
                        _receivedLength += bytesReceived - headersEndIndex - 4; // 4 is the length of "\r\n\r\n"
                    }

                }
                else
                {
                    _receivedLength += bytesReceived;
                }
                if (_contentLength != -1 && _receivedLength >= _contentLength)
                {
                    Console.WriteLine("Download complete");
                    _socket.Close();
                }
                else
                {
                    _socket.BeginReceive(_buffer, 0, _buffer.Length, SocketFlags.None, OnReceive, null);
                }
            }
        }
        private void ParseHeaders(string headers)
        {
            Console.WriteLine("Parsing headers...");
            foreach (var line in headers.Split(new[] { "\r\n" }, StringSplitOptions.RemoveEmptyEntries))
            {
                if (line.StartsWith("Content-Length:", StringComparison.OrdinalIgnoreCase))
                {
                    if (int.TryParse(line.Substring(15).Trim(), out int contentLength))
                    {
                        _contentLength = contentLength;
                        Console.WriteLine($"Content-Length found: {_contentLength}");
                    }
                }
            }
            if (_contentLength == -1)
            {
                Console.WriteLine("Content-Length not found, unable to determine body length.");
            }
        }
    }
}