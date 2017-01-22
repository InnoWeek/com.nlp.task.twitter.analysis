package com.nlp.task.twitter.analysis.index;

import java.io.IOException;

/**
 *
 */
public interface IndexSearcherFactory {
    ShareableIndexSearcher getIndexSearcher() throws IOException;
}
