package jp.riken.clustering.dcut;

final class Dcut$Pick {
   private final double score;
   private final int child;
   private final int parent;

   public Dcut$Pick(double score, int child, int parent) {
      this.score = score;
      this.child = child;
      this.parent = parent;
   }

   // $FF: synthetic method
   static double access$000(Dcut$Pick x0) {
      return x0.score;
   }

   // $FF: synthetic method
   static int access$100(Dcut$Pick x0) {
      return x0.child;
   }

   // $FF: synthetic method
   static int access$200(Dcut$Pick x0) {
      return x0.parent;
   }
}
