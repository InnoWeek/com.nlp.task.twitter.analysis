package com.nlp.task.twitter.analysis.index;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 */
public final class ShareableIndexSearcher extends IndexSearcher implements Closeable {
    private final IndexReader indexReader;
    private final long maxAge;
    private long creationDate;

    public ShareableIndexSearcher(IndexReader r, long maxAge) {
        super(r);
        this.indexReader = r;
        this.maxAge = maxAge;
        this.creationDate = System.currentTimeMillis();
    }

    ShareableIndexSearcher share() {
        indexReader.incRef();
        return this;
    }

    boolean isOutdated() {
        return (System.currentTimeMillis() - creationDate) > maxAge;
    }


    @Override
    public void close() throws IOException {
        indexReader.close();
    }
}
