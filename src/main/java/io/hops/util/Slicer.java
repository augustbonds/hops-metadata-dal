/*
 * Copyright (C) 2015 hops.io.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hops.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Slicer {

  public interface OperationHandler {

    public void handle(int startIndex, int endIndex) throws Exception;
  }

  public static void slice(final int total, final int sliceSize, final int nbThreads, OperationHandler op) throws
      Exception {
    if (total == 0) {
      return;
    }
    int numOfSlices;
    if (total <= sliceSize) {
      numOfSlices = 1;
    } else {
      numOfSlices = (int) Math.ceil(((double) total) / sliceSize);
    }

    ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
    List<Future<Object>> futures = new ArrayList<>();
    
    for (int slice = 0; slice < numOfSlices; slice++) {
      int startIndex = slice * sliceSize;
      int endIndex = Math.min((slice + 1) * sliceSize, total);
      futures.add(executor.submit(new SliceRunner(op, startIndex, endIndex)));
    }

    for(Future<Object> futur: futures){
      futur.get();
    }
    
    executor.shutdown();

    executor.awaitTermination(10, TimeUnit.SECONDS);

  }

  private static class SliceRunner implements Callable<Object> {

    final OperationHandler op;
    final int startIndex;
    final int endIndex;

    public SliceRunner(OperationHandler op, int startIndex, int endIndex) {
      this.op = op;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }

    @Override
    public Object call() throws Exception{
      op.handle(startIndex, endIndex);
      return null;
    }
  }
}
