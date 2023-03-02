Tema 2 APD
    Nicolae Alexandru-Dimitrie, 336 CA

    In aceasta tema, am folosit 3 clase : Tema2, ProductsRunnable, OrderRunnable pentru a incerca sa rezolv cerintele acesteia.

    In clasa Tema2, am retinut path-ul fisierului si numarul de threaduri primite ca argumente.Apoi,pornesc cele 2 pool-uri,unul
pentru fisierele de comenzi si unul pentru fisierele de produse.Dupa aceea,deschid fiserul "orders.txt" si incep sa citesc din
acesta.Dupa,folosesc metoda submit a orderPool-ului pentru a introduce un task OrderRunnable in pool.In plus,mai creez si golesc
fisierele de output ce vor fi verificate de checker.

    In clasa OrderRunnable, am implementat in metoda run citirea datelor din fisierul "orders.txt" si apoi prelucrarea acestora pentru
a le trimite pool-ului cu threadurile de nivel 2.In plus, am si calculat in variabila totalProducts numarul de produse dintr-un fisier
de tipul "orders.txt" pentru a o trimite si pe aceasta productsPool care prin urmatoarea clasa ProductsRunnable va efectua rezolvarea
cerintei temei.De asemenea,am mai si citit cu un nou reader,productReader,din fisierul "order_products.txt" corespunzator directorului
din calea primita ca parametru.In final,am inchis reader-ul ce citea din fisierul "orders.txt" si inchidea pool-ul pentru threadurile
de nivel 1.

    In clasa ProductsRunnable,am implementat in metoda run citirea datelor din fisierul "order_products.txt" si conditiile pentru ca un
produs sa fie livrat.In plus, am extras din linia citita id-ul comenzii si id-ul produsului si apoi am verificat daca id-ul comenzii extras
din linia citita din fisierul "orders.txt" este egala cu id-ul comenzii extrase din linia citita din "order_products.txt".Dupa, am scris in
fisierul de output "order_products_out" produsele ce vor fi considerate livrate.Apoi,am verificat daca numarul de produse prelucrate de pool-
ul de nivel 2 este diferit de 0 si este egal cu numarul de produse extras din linia citita din fisierul "orders.txt", daca acest lucru se intampla
comanda va fi considerata livrata si se va scrie in fisierul de output "order_products_out.txt".In final, am verificat daca numarul total de produse
ale tuturor comenzilor dintr-un fisier ajunge la 0,pentru a inchide reader-ul ce citea din fisierele de tipul "order_products.txt" si pentru a
opri pool-ul de nivelul 2.