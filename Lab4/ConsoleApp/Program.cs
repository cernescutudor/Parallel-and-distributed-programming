// See https://aka.ms/new-console-template for more information


using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using ConsoleApp;


namespace ConsoleApp
{
    class Program
    {
        static async Task Main(string[] args)
        {
            string host = "sepse.ro";
            string path = "/st/repartitoare.html";

            EventDrivenDownloader downloader1 = new EventDrivenDownloader(host, path);

            TaskBasedDownloader downloader2 = new TaskBasedDownloader(host, path);

            AsyncAwaitDownloader downloader3 = new AsyncAwaitDownloader(host, path);
            

            downloader1.Start();
            Task task2 = downloader2.Start();
            Task task3 = downloader3.StartAsync();

            await Task.WhenAll(task2, task3);

        }
    }
}