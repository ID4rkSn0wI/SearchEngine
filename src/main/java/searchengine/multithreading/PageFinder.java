//package searchengine.multithreading;
//
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import searchengine.database.DBConnection;
//import searchengine.model.Page;
//import searchengine.model.Site;
//import searchengine.interfaces.indexing.PageInterface;
//
//import java.io.IOException;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.RecursiveAction;
//
//import static java.lang.Thread.sleep;
//
//public class PageFinder extends RecursiveAction {
//    private PageInterface pageRepository;
//    private final String url;
//    private final Site rootUrl;
//
//    public PageFinder(String url, Site rootUrl, PageInterface pageRepository) {
//        this.pageRepository = pageRepository;
//        this.url = url;
//        this.rootUrl = rootUrl;
//    }
//
//
//    @Override
//    protected void compute() {
//        List<PageFinder> taskList = new ArrayList<>();
//        List<String> urls = new ArrayList<>();
//
//
//        try
//        {
//            sleep(100);
//            Page page = new Page();
//
//            page.setSite_id(rootUrl.getId());
//            page.setPath(url);
//            Connection con = Jsoup.connect(url)
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                    .referrer("http://www.google.com")
//                    .timeout(100000);
//            page.setCode(con.execute().statusCode());
//            Document doc = con.get();
//            page.setContent(doc.toString());
//            if (addPage(page)){
//                Elements links = doc.select("a[href]");
//                for (Element link : links) {
//                    String branchUrl = link.attr("abs:href");
//                    if (checkIfCorrectLink(branchUrl, rootUrl.getUrl())) {
//                        urls.add(branchUrl);
//                    }
//                }
//            }
//        }
//        catch (IOException | InterruptedException e)
//        {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
////        long start = System.currentTimeMillis();
////        System.out.println(System.currentTimeMillis() - start + " ms!!!!!!!!!!!!!!!!!");
//        for (String link : urls)
//        {
//            PageFinder task = new PageFinder(link, rootUrl, pageRepository);
//            task.fork();
//            taskList.add(task);ф
//        }
//
//        for (PageFinder task : taskList)
//        {
//            task.join();
//        }
//    }
//
//    private boolean checkIfCorrectLink(String url, String rootUrl) throws SQLException {
//        return url.startsWith(rootUrl) && !url.contains("#") &&
//                !url.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)") && !checkPage(url);
//    }
//
//    private boolean addPage(Page page) throws SQLException {
//        if (!checkPage(page.getPath())) {
//            pageRepository.save(page);
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkPage(String siteUrl) throws SQLException {
//        ResultSet resultSet = DBConnection.getConnection()
//                .createStatement()
//                .executeQuery("SELECT id FROM page WHERE path=" + "'" + siteUrl + "'");
//        return resultSet.next();
//    }
//}
