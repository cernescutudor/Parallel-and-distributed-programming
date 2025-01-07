import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.locks.*;

class HamiltonianCycle {
    private Graph graph;
    private ExecutorService executor;
    private volatile boolean foundCycle = false;
    private ReentrantLock lock = new ReentrantLock();
    private CountDownLatch latch;

    public HamiltonianCycle(Graph graph, int numThreads) {
        this.graph = graph;
        this.executor = Executors.newFixedThreadPool(numThreads);
        this.latch = new CountDownLatch(1);
    }

    public void findHamiltonianCycle(ArrayList<Integer> path, Set<Integer> visited, int startNode) {
        for (int neighbor : graph.getNeighbors(path.get(path.size() - 1))) {
            if (neighbor == startNode && path.size() == graph.getVertexCount()) {
                path.add(neighbor);
                System.out.println(path);
                lock.lock();
                try {
                    if (!foundCycle) {
                        foundCycle = true;
                        System.out.println("Found cycle");
                        latch.countDown(); // Signal that the cycle is found
                    }
                } finally {
                    lock.unlock();
                }
                return;
            }
            if (!visited.contains(neighbor)) {
                ArrayList<Integer> newPath = new ArrayList<>(path);
                Set<Integer> newVisited = new HashSet<>(visited);
                newPath.add(neighbor);
                newVisited.add(neighbor);
                synchronized (this) {
                    if (foundCycle) {
                        Thread.currentThread().yield();
                        return;
                        
                    }
                }
                executor.execute(() -> findHamiltonianCycle(newPath, newVisited, startNode));
            }
            Thread.currentThread().yield();
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph("graph2.txt");
        //System.out.println(graph.toString());
        HamiltonianCycle hc = new HamiltonianCycle(graph, 4);
        ArrayList<Integer> path = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        path.add(8);
        visited.add( 8  );
        hc.executor.execute(() -> hc.findHamiltonianCycle(path, visited, 8));
        
        try {
            hc.latch.await(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            hc.shutdown();
        }
    }
}
