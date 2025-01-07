import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Graph {
    private int V;
    private int E;
    private Map<Integer, List<Integer>> adj;

    public Graph(String filename) {
        adj = new HashMap<Integer, List<Integer>>();
        readGraph(filename);

    }

    public void readGraph(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            this.V = Integer.parseInt(line.split(" ")[0]);
            this.E = Integer.parseInt(line.split(" ")[1]);
            for( int i = 0; i < E; i++) {
                line = reader.readLine();
                int a = Integer.parseInt(line.split(" ")[0]);
                int b = Integer.parseInt(line.split(" ")[1]);
                if( !adj.containsKey(a) ) {
                    adj.put(a, new ArrayList<Integer>());
                }
                if( !adj.containsKey(b) ) {
                    adj.put(b, new ArrayList<Integer>());
                }
                adj.get(a).add(b);
                
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getVertexCount() {
        return V;
    }

    public List<Integer> getNeighbors( int v ) {
        return adj.getOrDefault(v, new ArrayList<Integer>());
    }

    public HashMap<Integer, List<Integer>> getAdj() {
        return (HashMap<Integer, List<Integer>>) adj;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(V + " vertices, " + E + " edges\n");
        for( int v : adj.keySet() ) {
            sb.append(v + ": ");
            for( int w : adj.get(v) ) {
                sb.append(w + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    


    

}