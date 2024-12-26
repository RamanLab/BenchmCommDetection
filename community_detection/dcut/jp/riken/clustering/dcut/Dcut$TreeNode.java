package jp.riken.clustering.dcut;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

final class Dcut$TreeNode {
   private final int id;
   private Dcut$TreeNode parent = null;
   private final Set<Dcut$TreeNode> children = new HashSet();
   private double size = 0.0D;
   private double d = Double.MAX_VALUE;
   private double fragment_size = 0.0D;
   private String extId;
   private final double minModuleSize;

   public Dcut$TreeNode(int id, double minSize) {
      this.id = id;
      this.size = 1.0D;
      this.minModuleSize = minSize;
   }

   public Dcut$TreeNode(int id, Dcut$TreeNode parent, double d, int size, double minSize) {
      this.id = id;
      this.parent = parent;
      this.d = d;
      this.size = (double)size;
      this.minModuleSize = minSize;
   }

   public List<String> collectMemberIds() {
      List<String> result = new ArrayList();
      Queue<Dcut$TreeNode> q = new ArrayDeque();
      q.add(this);

      while(!q.isEmpty()) {
         Dcut$TreeNode n = (Dcut$TreeNode)q.poll();
         result.add(n.extId);
         q.addAll(n.children);
      }

      return result;
   }

   public double getSize() {
      return this.size;
   }

   public double getD() {
      return this.d;
   }

   public double getFragment_size() {
      return this.fragment_size;
   }

   public void setFragment_size(double fragment_size) {
      this.fragment_size = fragment_size;
   }

   public String getExtId() {
      return this.extId;
   }

   public void setExtId(String extId) {
      this.extId = extId;
   }

   public Dcut$TreeNode getParent() {
      return this.parent;
   }

   public double getScore() {
      return this.d;
   }

   public int getId() {
      return this.id;
   }

   public Dcut$TreeNode grow(int newNodeId, double d) {
      ++this.size;
      if (this.parent != null) {
         this.parent.updateSize(1.0D);
      }

      Dcut$TreeNode c = new Dcut$TreeNode(newNodeId, this, d, 1, this.minModuleSize);
      this.children.add(c);
      return c;
   }

   public double updateSize(double change) {
      this.size += change;
      if (this.parent != null) {
         return this.parent.updateSize(change);
      } else {
         this.fragment_size += change;
         return this.size;
      }
   }

   public Dcut$TreeNode findCutPoint() {
      List<Dcut$TreeNode> _children = new ArrayList();
      TDoubleArrayList scores = new TDoubleArrayList();
      Queue<Dcut$TreeNode> q = new ArrayDeque();
      q.add(this);

      while(true) {
         Dcut$TreeNode c;
         double aboveSize;
         do {
            Dcut$TreeNode p;
            do {
               if (q.isEmpty()) {
                  c = null;
                  double minScore = Double.MAX_VALUE;

                  for(int i = 0; i < scores.size(); ++i) {
                     if (scores.get(i) < minScore) {
                        minScore = scores.get(i);
                        c = (Dcut$TreeNode)_children.get(i);
                     }
                  }

                  return c;
               }

               c = (Dcut$TreeNode)q.poll();
               q.addAll(c.children);
               p = c.getParent();
            } while(p == null);

            aboveSize = this.fragment_size - c.size;
         } while(!(aboveSize >= 3.0D) && !(c.size >= 3.0D));

         _children.add(c);
         scores.add(c.d / Math.min(aboveSize, c.size));
      }
   }

   public Dcut$TreeNode _findCutPoint() {
      List<Dcut$TreeNode> _children = new ArrayList();
      TDoubleArrayList scores = new TDoubleArrayList();
      Queue<Dcut$TreeNode> q = new ArrayDeque();
      q.add(this);

      while(true) {
         Dcut$TreeNode c;
         double aboveSize;
         do {
            Dcut$TreeNode p;
            do {
               if (q.isEmpty()) {
                  c = null;
                  double maxScore = Double.MIN_VALUE;

                  for(int i = 0; i < scores.size(); ++i) {
                     if (scores.get(i) > maxScore) {
                        maxScore = scores.get(i);
                        c = (Dcut$TreeNode)_children.get(i);
                     }
                  }

                  return c;
               }

               c = (Dcut$TreeNode)q.poll();
               q.addAll(c.children);
               p = c.getParent();
            } while(p == null);

            aboveSize = this.fragment_size - c.size;
         } while(!(aboveSize >= this.minModuleSize) && !(c.size >= this.minModuleSize));

         _children.add(c);
         scores.add(c.d / Math.max(aboveSize, c.size));
      }
   }

   public void cut() {
      this.fragment_size = this.size;
      this.parent.updateSize(-this.size);
      this.parent.children.remove(this);
      this.parent = null;
      this.d = Double.MAX_VALUE;
   }

   // $FF: synthetic method
   static double access$300(Dcut$TreeNode x0) {
      return x0.fragment_size;
   }
}
