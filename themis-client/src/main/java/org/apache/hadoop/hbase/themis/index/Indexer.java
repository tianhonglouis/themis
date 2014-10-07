package org.apache.hadoop.hbase.themis.index;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.themis.ThemisGet;
import org.apache.hadoop.hbase.themis.ThemisScan;
import org.apache.hadoop.hbase.themis.ThemisScanner;
import org.apache.hadoop.hbase.themis.TransactionConstant;
import org.apache.hadoop.hbase.themis.cache.ColumnMutationCache;

public abstract class Indexer {
  private static Indexer indexer = null;
  private static Object lock = new Object();
  
  public static Indexer getIndexer(Configuration conf) throws IOException {
    if (indexer == null) {
      String indexerCls = conf.get(TransactionConstant.INDEXER_CLASS_KEY,
        TransactionConstant.DEFAULT_INDEXER_CLASS);
      synchronized (lock) {
        if (indexer == null) {
          try {
            indexer = (Indexer) Class.forName(indexerCls).getConstructor(Configuration.class)
                .newInstance(conf);
          } catch (Exception e) {
            throw new IOException(e);
          }
        }
      }
    }
    return indexer;
  }

  public Indexer(Configuration conf) {
  }
  
  public abstract Result get(byte[] tableName, ThemisGet get) throws IOException;
  public abstract ThemisScanner getScanner(byte[] tableName, ThemisScan scan) throws IOException;
  public abstract void addIndexMutations(ColumnMutationCache mutationCache) throws IOException;
  
  // NullIndexer do nothing
  public static class NullIndexer extends Indexer {
    public NullIndexer(Configuration conf) {
      super(conf);
    }
    
    @Override
    public Result get(byte[] tableName, ThemisGet get) throws IOException {
      return null;
    }

    @Override
    public ThemisScanner getScanner(byte[] tableName, ThemisScan scan) throws IOException {
      return null;
    }

    @Override
    public void addIndexMutations(ColumnMutationCache mutationCache) throws IOException {
    }
  }
}