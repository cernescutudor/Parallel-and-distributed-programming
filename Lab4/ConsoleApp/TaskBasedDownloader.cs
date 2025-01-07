using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace ConsoleApp
{
    public class TaskBasedDownloader
    {
        private readonly string _host;
        private readonly string _path;
        private readonly Socket _socket;
        private readonly byte[] _buffer = new byte[1024]; 
        private bool _headersComplete = false; 
        private StringBuilder _headers = new StringBuilder(); 
        private int _contentLength = -1; 

        public TaskBasedDownloader(string host, string path)
        {
            _host = host;
            _path = path;
            _socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        }

        public Task Start()
        {
            Console.WriteLine("Starting connection...");

            return ConnectAsync()
                .ContinueWith(connectTask =>
                {
                    Console.WriteLine("Connected to server.");
                    byte[] requestBytes = CreateGetRequest();
                    Console.WriteLine("Sending HTTP GET request...");
                    return SendAsync(requestBytes);
                })
                .Unwrap() 
                .ContinueWith(sendTask =>
                {
                    Console.WriteLine("Request sent.");
                    return ReceiveHeadersAsync();
                })
                .Unwrap()
                .ContinueWith(receiveTask =>
                {
                    Console.WriteLine("Headers received and parsed.");
                    Console.WriteLine("Connection closed after parsing headers.");
                    _socket.Close();
                });
        }

        private Task ConnectAsync()
        {
            var tcs = new TaskCompletionSource<bool>();
            _socket.BeginConnect(_host, 80, ar =>
            {
                try
                {
                    _socket.EndConnect(ar);
                    tcs.SetResult(true);
                }
                catch (Exception ex)
                {
                    tcs.SetException(ex);
                }
            }, null);
            return tcs.Task;
        }

        private Task SendAsync(byte[] data)
        {
            var tcs = new TaskCompletionSource<bool>();
            _socket.BeginSend(data, 0, data.Length, SocketFlags.None, ar =>
            {
                try
                {
                    _socket.EndSend(ar);
                    tcs.SetResult(true);
                }
                catch (Exception ex)
                {
                    tcs.SetException(ex);
                }
            }, null);
            return tcs.Task;
        }


        private Task ReceiveHeadersAsync()
        {
            var tcs = new TaskCompletionSource<bool>(); 

            void ReceiveCallback()
            {
                ReceiveAsync().ContinueWith(receiveResultTask =>
                {
                    int bytesReceived = receiveResultTask.Result;
                    if (bytesReceived > 0 && !_headersComplete)
                    {
                       
                        string responsePart = Encoding.ASCII.GetString(_buffer, 0, bytesReceived);
                        _headers.Append(responsePart);

                        // Check if headers are complete (indicated by \r\n\r\n)
                        int headersEndIndex = _headers.ToString().IndexOf("\r\n\r\n", StringComparison.Ordinal);
                        if (headersEndIndex != -1)
                        {
                            _headersComplete = true;

                            
                            string headerSection = _headers.ToString().Substring(0, headersEndIndex);
                            ParseHeaders(headerSection);

                            
                            tcs.SetResult(true);  
                        }
                        else
                        {
                           
                            ReceiveCallback();
                        }
                    }
                });
            }

            
            ReceiveCallback();
            return tcs.Task; 
        }

        private Task<int> ReceiveAsync()
        {
            var tcs = new TaskCompletionSource<int>();
            _socket.BeginReceive(_buffer, 0, _buffer.Length, SocketFlags.None, ar =>
            {
                try
                {
                    int bytesReceived = _socket.EndReceive(ar);
                    tcs.SetResult(bytesReceived);
                }
                catch (Exception ex)
                {
                    tcs.SetException(ex);
                }
            }, null);
            return tcs.Task;
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
                Console.WriteLine("Content-Length not found in headers.");
            }
        }

        private byte[] CreateGetRequest()
        {
            string request = $"GET {_path} HTTP/1.1\r\n" +
                             $"Host: {_host}\r\n" +
                             "Connection: close\r\n\r\n";
            return Encoding.ASCII.GetBytes(request);
        }
    }



}