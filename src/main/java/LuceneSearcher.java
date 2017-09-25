import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import sun.misc.Version;

import static org.apache.lucene.util.Version.*;

public class LuceneSearcher {

  private final Directory directory;

  public LuceneSearcher(){
    this.directory = new RAMDirectory();
  }

  private Document createPartDocument(Part part) {
    Document document = new Document();
    document.add(new TextField("partName", part.getName(), Field.Store.YES));
    document.add(new TextField("partDesc", part.getDescription(), Field.Store.YES));
    return document;
  }

  private IndexWriter createWriter() throws IOException {
    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
    return new IndexWriter(directory, config);
  }

  public void createPartIndex(List<Part> parts) throws Exception {
    IndexWriter writer = createWriter();
    for(Part p : parts){
      writer.addDocument(createPartDocument(p));
    }
    writer.commit();
    writer.close();
  }

  private IndexSearcher createSearcher() throws IOException {
    IndexReader reader = DirectoryReader.open(directory);
    return new IndexSearcher(reader);
  }

  public List<Part> searchByPartName(String partName)
      throws Exception {
    IndexSearcher searcher = createSearcher();
    QueryParser qp = new QueryParser("partName", new StandardAnalyzer());
    Query query = qp.parse(partName);
    return createPartsFromHits(searcher, searcher.search(query, Integer.MAX_VALUE).scoreDocs);
  }

  public List<Part> prefixSearchByPartName(String partName)
      throws Exception {
    IndexSearcher searcher = createSearcher();

    Term term = new Term("partName", partName.toLowerCase());
    Query query = new PrefixQuery(term);

    return createPartsFromHits(searcher, searcher.search(query, Integer.MAX_VALUE).scoreDocs);
  }

  private List<Part> createPartsFromHits(IndexSearcher searcher, ScoreDoc[] hits)
      throws IOException {
    List<Part> response = new ArrayList<>();
    for (ScoreDoc sd : hits) {
      Document d = searcher.doc(sd.doc);
      Part part = new Part(d.get("partName"), d.get("partDesc"));
      response.add(part);
    }
    return response;
  }
}
