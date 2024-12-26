package jp.riken.clustering.dcut;

import gnu.trove.list.array.TIntArrayList;
import java.util.Queue;
import jp.riken.clustering.dcut.Dcut.Pick;

final class Dcut$Picker implements Runnable {
   private final int from;
   private final int to;
   private final double[][] array;
   private final TIntArrayList unprocessed;
   private final TIntArrayList processed;
   private final Queue<Pick> out;

   public Dcut$Picker(int from, int to, double[][] array, TIntArrayList unprocessed, TIntArrayList processed, Queue<Pick> out) {
      this.from = from;
      this.to = to;
      this.array = array;
      this.unprocessed = unprocessed;
      this.processed = processed;
      this.out = out;
   }

   public void run() {
      for(int i = this.from; i < this.to; ++i) {
         double max = -1.0D;
         int childe = -1;
         int parent = -1;
         int val = this.processed.get(i);

         for(int j = 0; j < this.unprocessed.size(); ++j) {
            int child = this.unprocessed.get(j);
            if (this.array[val][child] > max) {
               max = this.array[val][child];
               childe = child;
               parent = val;
            }
         }

         this.out.add(new Pick(max, childe, parent));
      }

   }
}
