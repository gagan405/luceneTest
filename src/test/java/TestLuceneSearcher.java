import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestLuceneSearcher {

  private LuceneSearcher luceneSearcher = new LuceneSearcher();

  @Test
  public void testStandardSearch() throws Exception {
    List<Part> parts = new ArrayList<>();
    parts.add(new Part("Battery", "We need batteries"));
    parts.add(new Part("Tyre", "We need tyres of course"));
    parts.add(new Part("Steering Wheel", "We need this as well"));
    parts.add(new Part("Steering Wheel 2", "We need this as well"));
    luceneSearcher.createPartIndex(parts);

    List<Part> results = luceneSearcher.searchByPartName("tyre");
    Assert.assertTrue(results.size() == 1);
  }

  @Test
  public void testPrefixSearch() throws Exception {
    List<Part> parts = new ArrayList<>();
    parts.add(new Part("Battery", "We need batteries"));
    parts.add(new Part("Tyre", "We need tyres of course"));
    parts.add(new Part("Steering Wheel", "We need this as well"));
    parts.add(new Part("Steering Wheel 2", "We need this as well"));
    luceneSearcher.createPartIndex(parts);

    List<Part> results = luceneSearcher.prefixSearchByPartName("sTe");
    Assert.assertTrue(results.size() == 2);
  }

}
