import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductsRunnable implements Runnable {
    private final String path;//calea directorului
    private final int nrThreads;//numarul de threaduri
    private final Scanner productsReader;//reader-ul ce va citi cate o linie din fisierul "order_products.txt"
    private final ExecutorService productsPool;//pool-ul pentru thread-urile de nivel 2 ce prelucreaza fisierul de produse
    private final AtomicInteger productsQueue;//contorul pentru productsPool
    private final AtomicInteger totalProducts;//contorul pentru numarul total de produse din toate comenzile din fisier
    private final String orderId;//id-ul unei comenzi
    private final Integer nrProducts;//numarul de produse al unei comenzi

    //Initializarea celor doi writeri care vor scrie in cele 2 fisiere de output
    FileWriter myWriter = null;
    FileWriter myWriter2 = null;

    //Constructor
    public ProductsRunnable(String path, ExecutorService productsPool,int nrThreads, AtomicInteger inQueue,String orderId,Integer nrProducts,AtomicInteger totalProducts,Scanner myReader) {
        this.path = path;
        this.productsPool = productsPool;
        this.nrThreads = nrThreads;
        this.productsQueue = inQueue;
        this.orderId = orderId;
        this.nrProducts = nrProducts;
        this.totalProducts = totalProducts;
        this.productsReader = myReader;
    }

    @Override
    public void run() {
       String data2 = null;//initializarea variabilei ce va retine fiecare linie citita din fisierul "order_products.txt"

        //Citirea a cate o linie din fisierul "order_products.txt"
       if (productsReader.hasNextLine()) {
           data2 = productsReader.nextLine();
       }

        //Extragerea id-ului comenzii si a id-ului produsului
        StringTokenizer st2 = new StringTokenizer(data2,",");
        String orderId2 = st2.nextToken();
        String productId = st2.nextToken();

        //Verificare pentru ca un produs sa fie livrat
        if(orderId.equals(orderId2)){
            productsQueue.incrementAndGet();
            totalProducts.decrementAndGet();
            if (totalProducts.get() >= 0 && productsQueue.get() <= nrProducts) {
                try {
                    //Scrierea in fisierul de output "order_products_out.txt"
                    myWriter = new FileWriter("order_products_out.txt", true);
                    myWriter.append(orderId2 + "," + productId + ",shipped" + "\n");
                    myWriter.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        }

        //Verificarea pentru ca o comanda sa fie livrata
        if(productsQueue.get() == nrProducts && productsQueue.get() != 0){
            try {
                myWriter2 = new FileWriter("orders_out.txt", true);
                myWriter2.append(orderId + "," + nrProducts + ",shipped" + "\n");
                myWriter2.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        //Trimiterea unui nou task in productsPool
        if (productsQueue.get() < nrProducts) {
            productsPool.submit(new ProductsRunnable(path, productsPool, nrThreads, productsQueue, orderId, nrProducts, totalProducts, productsReader));
        }

        //Verificarea pentru a opri reader-ul ce citea din "order_products.txt" si pentru a inchide pool-ul pentru threadurile de nivel 2
        if (totalProducts.get() <= 0) {
            productsReader.close();
            productsPool.shutdown();
        }
    }
}