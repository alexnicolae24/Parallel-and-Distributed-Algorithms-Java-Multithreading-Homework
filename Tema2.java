import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        String path = args[0]; //extragerea caii directorului cu fisierele de input
        int nrThreads = Integer.parseInt(args[1]); //extragerea numarului de threaduri pe care se va rula fiserul de test

        //Creearea pool-ului pentru pool-ul ce se ocupa cu prelucrarea comenzilor
        AtomicInteger orderQueue = new AtomicInteger(0);
        ExecutorService orderPool = Executors.newFixedThreadPool(nrThreads);

        //Creearea pool-ului pentru pool-ul ce se ocupa cu prelucrarea produselor din comenzi
        AtomicInteger productsQueue = new AtomicInteger(0);
        ExecutorService productsPool = Executors.newFixedThreadPool(nrThreads);

        //Deschiderea unui fisier "orders.txt" si declararea unui reader
        File ordersFile = new File(path + "/orders.txt");
        Scanner myReader = new Scanner(ordersFile);

        //Trimiterea unui task catre pool-ul de comenzi
        orderQueue.incrementAndGet();
        orderPool.submit(new OrderRunnable(path, orderPool, productsPool, nrThreads, orderQueue, productsQueue, new AtomicInteger(0), myReader));

        //Creearea si stergerea continutului din fisierul de output "orders_out.txt"
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("orders_out.txt"));
        writer.flush();

        //Creearea si stergerea continutului din fisierul de output "order_products_out.txt"
        writer = Files.newBufferedWriter(Paths.get("order_products_out.txt"));
        writer.flush();
    }
}
