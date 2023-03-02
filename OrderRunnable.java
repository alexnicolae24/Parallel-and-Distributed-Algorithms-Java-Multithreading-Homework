import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderRunnable implements Runnable {
    private final String path;//calea directorului
    private final int nrThreads;//numarul de threaduri
    private final ExecutorService orderPool;//pool-ul pentru thread-urile de nivel 1 ce prelucreaza fisierul de comenzi
    private final ExecutorService productsPool;//pool-ul pentru thread-urile de nivel 2 ce prelucreaza fisierul de produse
    private final AtomicInteger orderQueue;//contorul pentru orderPool
    private final AtomicInteger productsQueue;//contorul pentru productsPool
    private final AtomicInteger totalProducts;//contorul pentru numarul total de produse din toate comenzile din fisier
    private final Scanner myReader;//reader-ul ce va citi cate o linie din fisierul "orders.txt"



    //Constructor
    public OrderRunnable(String path, ExecutorService orderPool, ExecutorService productsPool,  int nrThreads, AtomicInteger inQueue1, AtomicInteger inQueue2, AtomicInteger totalProducts, Scanner myReader) {
        this.path = path;
        this.orderPool = orderPool;
        this.productsPool = productsPool;
        this.nrThreads = nrThreads;
        this.orderQueue = inQueue1;
        this.productsQueue = inQueue2;
        this.totalProducts = totalProducts;
        this.myReader = myReader;
    }

    @Override
    public void run() {
        String data = null;//initializarea variabilei ce va retine fiecare linie citita din fisierul "orders.txt"

        //Citirea a cate o linie din fisierul "orders.txt" si trimiterea task-ului in orderPool
        if (myReader.hasNextLine()) {
            data = myReader.nextLine();
            orderQueue.incrementAndGet();
            orderPool.submit(new OrderRunnable(path , orderPool, productsPool, nrThreads, orderQueue, productsQueue,totalProducts, myReader));
        }

        if(data != null) {
            //Extragerea id-ului comenzii si a numarului de produse din comanda
            StringTokenizer st = new StringTokenizer(data,",");
            String orderId = st.nextToken();
            int nrProducts = Integer.parseInt(st.nextToken());
            totalProducts.addAndGet(nrProducts);//calcularea numarului total de produse ale tuturor comenzilor dintr-un fisier de comenzi
            productsQueue.incrementAndGet();
            Scanner productReader = null;//initializarea unui reader pentru fisierul de produse
            try {
                File productsFile = new File(path + "/order_products.txt");//creearea fisierului de produse
                productReader = new Scanner(productsFile);//declararea unui reader ce va citi din fisierul "order_products.txt"
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //Trimiterea unui task catre pool-ul de nivelul 2
            productsPool.submit(new ProductsRunnable(path , productsPool, nrThreads, new AtomicInteger(0),orderId, nrProducts,
                    totalProducts, productReader));
        }

        //Verificarea pentru a opri reader-ul ce citea din "orders.txt" si pentru a inchide pool-ul pentru threadurile de nivel 1
        int left = orderQueue.decrementAndGet();
        if (left == 0) {
            orderPool.shutdown();
            myReader.close();
        }
    }
}
