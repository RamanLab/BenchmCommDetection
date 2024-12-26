package jp.riken.clustering.dcut;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jp.riken.clustering.misc.DefaultSuppliers;

public class Dcut {
   public static void runDcut(String file, int threads, int maxModuleSize, int minModuleSize, boolean experimenatal) throws IOException {
      SetMultimap<String, String> map = Multimaps.newSetMultimap(Maps.newHashMap(), DefaultSuppliers.set());
      String base = file.replace(".tab", "");
      base = base.replace(".txt", "");
      Set<String> set = new HashSet();
      BufferedReader br = new BufferedReader(new FileReader(file));
      double max = 0.0D;
      double min = 0.0D;

      String line;
      while((line = br.readLine()) != null) {
         String[] data = line.split("\t");
         set.add(data[0]);
         set.add(data[1]);
         map.put(data[0], data[1]);
         map.put(data[1], data[0]);
         map.put(data[0], data[0]);
         map.put(data[1], data[1]);
         if (!data[2].contains("Inf") && !data[2].contains("NaN")) {
            try {
               double v = Double.valueOf(data[2]);
               if (v > max) {
                  max = v;
               }

               if (v < min) {
                  min = v;
               }
            } catch (NumberFormatException var39) {
            }
         }
      }

      br.close();
      double[][] array = new double[set.size()][set.size()];
      List<String> list = new ArrayList(set);
      Map<String, Integer> mapper = new HashMap();
      Map<Integer, String> unmapper = new HashMap();

      for(int i = 0; i < list.size(); ++i) {
         mapper.put(list.get(i), i);
         unmapper.put(i, list.get(i));
      }

      list = null;

      double weight;
      int i;
      int j;
      for(br = new BufferedReader(new FileReader(file)); (line = br.readLine()) != null; array[j][i] = weight) {
         String[] data = line.split("\t");
         double value = 1.0D;
         boolean isSet = false;
         if (data[2].contains("Inf")) {
            value = max;
            isSet = true;
         }

         if (data[2].contains("NaN") || data[2].contains("-Inf")) {
            value = min;
            isSet = true;
         }

         if (!isSet) {
            value = Double.valueOf(data[2]);
            if (value < 0.0D) {
               value = Math.abs(value);
            }
         }

         weight = getJaccard(map.get(data[0]), map.get(data[1])) * (value / max);
         i = (Integer)mapper.get(data[0]);
         j = (Integer)mapper.get(data[1]);
         array[i][j] = weight;
      }

      br.close();

      ArrayBlockingQueue<jp.riken.clustering.dcut.Dcut.Pick> out = new ArrayBlockingQueue(array.length);
      TIntArrayList processed = new TIntArrayList();
      TIntArrayList unprocessed = new TIntArrayList();

      for(int i = 1; i < unmapper.size(); ++i) {
         unprocessed.add(i);
      }

      Map<Integer, jp.riken.clustering.dcut.Dcut.TreeNode> id2node = new HashMap();
      jp.riken.clustering.dcut.Dcut.TreeNode root = new jp.riken.clustering.dcut.Dcut.TreeNode(0, (double)minModuleSize);
      id2node.put(0, root);
      int a = 0;
      TIntArrayList toUpdate = new TIntArrayList();
      TDoubleArrayList processed_maxScore = new TDoubleArrayList();
      TIntArrayList processed_bestChild = new TIntArrayList();
      TIntIntHashMap processedPositions = new TIntIntHashMap();
      processedPositions.put(0, 0);
      processed.add(0);
      processed_maxScore.add(-1.0D);
      processed_bestChild.add(-1);
      toUpdate.add(0);
      ExecutorService executor = Executors.newFixedThreadPool(threads);
      Stopwatch stopwatch = Stopwatch.createStarted();

      int bestChild;
      while(unprocessed.size() > 0) {
         int N = toUpdate.size();
         int L = true;
         int L;
         if (N % threads == 0) {
            L = N / threads;
         } else {
            L = N / (threads - 1);
         }

         if (L == 0) {
            L = 1;
         }

         for(int i = 0; i < N; i += L) {
            executor.submit(new jp.riken.clustering.dcut.Dcut.Picker(i, Math.min(N, i + L), array, unprocessed, toUpdate, out));
         }

         double best = -1.0D;
         bestChild = -1;
         int bestParent = -1;

         int k;
         for(k = 0; k < processed_maxScore.size(); ++k) {
            if (processed_maxScore.get(k) > best && !toUpdate.contains(processed.get(k))) {
               best = processed_maxScore.get(k);
               bestChild = processed_bestChild.get(k);
               bestParent = processed.get(k);
            }
         }

         k = toUpdate.size();

         while(k > 0) {
            while(out.isEmpty()) {
            }

            jp.riken.clustering.dcut.Dcut.Pick p = (jp.riken.clustering.dcut.Dcut.Pick)out.poll();
            --k;
            if (jp.riken.clustering.dcut.Dcut.Pick.access$000(p) > best) {
               best = jp.riken.clustering.dcut.Dcut.Pick.access$000(p);
               bestChild = jp.riken.clustering.dcut.Dcut.Pick.access$100(p);
               bestParent = jp.riken.clustering.dcut.Dcut.Pick.access$200(p);
            }

            int index = processedPositions.get(jp.riken.clustering.dcut.Dcut.Pick.access$200(p));
            processed_maxScore.set(index, jp.riken.clustering.dcut.Dcut.Pick.access$000(p));
            processed_bestChild.set(index, jp.riken.clustering.dcut.Dcut.Pick.access$100(p));
         }

         toUpdate.clear();

         for(int k = 0; k < processed_bestChild.size(); ++k) {
            if (processed_bestChild.get(k) == bestChild) {
               toUpdate.add(processed.get(k));
            }
         }

         jp.riken.clustering.dcut.Dcut.TreeNode node = ((jp.riken.clustering.dcut.Dcut.TreeNode)id2node.get(bestParent)).grow(bestChild, best);
         unprocessed.remove(bestChild);
         processedPositions.put(bestChild, processed.size());
         processed.add(bestChild);
         processed_maxScore.add(-1.0D);
         processed_bestChild.add(-1);
         toUpdate.add(bestChild);
         id2node.put(bestChild, node);
         ++a;
         if (a > 999) {
            a = 0;
            stopwatch = Stopwatch.createStarted();
         }
      }

      var53 = id2node.entrySet().iterator();

      while(var53.hasNext()) {
         Entry<Integer, jp.riken.clustering.dcut.Dcut.TreeNode> ent = (Entry)var53.next();
         ((jp.riken.clustering.dcut.Dcut.TreeNode)ent.getValue()).setExtId((String)unmapper.get(ent.getKey()));
      }

      jp.riken.clustering.dcut.Dcut.TreeNode treeRoot = (jp.riken.clustering.dcut.Dcut.TreeNode)id2node.get(0);
      treeRoot.setFragment_size(treeRoot.getSize());
      List<List<String>> clusters = new ArrayList();
      Queue<jp.riken.clustering.dcut.Dcut.TreeNode> q = new ArrayDeque();
      q.add(treeRoot);

      while(!q.isEmpty()) {
         jp.riken.clustering.dcut.Dcut.TreeNode n1 = (jp.riken.clustering.dcut.Dcut.TreeNode)q.poll();
         if (jp.riken.clustering.dcut.Dcut.TreeNode.access$300(n1) <= (double)maxModuleSize) {
            clusters.add(n1.collectMemberIds());
         } else {
            jp.riken.clustering.dcut.Dcut.TreeNode n2;
            if (experimenatal) {
               n2 = n1._findCutPoint();
            } else {
               n2 = n1.findCutPoint();
            }

            n2.cut();
            q.add(n1);
            q.add(n2);
         }
      }

     System.out.println(clusters); 
   }
	

   private static double getJaccard(Collection<String> as, Collection<String> bs) {
      double intersection = 0.0D;
      Iterator var4 = as.iterator();

      while(var4.hasNext()) {
         String a = (String)var4.next();
         if (bs.contains(a)) {
            ++intersection;
         }
      }

      double total = (double)(as.size() + bs.size());
      return intersection / (total - intersection);
   }
}
