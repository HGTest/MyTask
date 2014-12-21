package twitterFetcherTask.twitterFetcherTask;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

import com.mongodb.DBCollection;
import twitter4j.Twitter;



public class TwitterFetcherTest {
	//Define Variables
	TwitterFetcher twitterFetcherObj=new TwitterFetcher();
	DBCollection tweetsCollectionObj;
	Twitter twitterObj;
	private static String consumerKeyParam = "p5bmolekIMqv3jvmosUc1I5ZT";
	private static String consumerSecretParam = "XqLqdS4TYR91V5MyQV6vvYibq6xlu3qXwUWqZQcBzAdho1Alv1";
	private static String accessTokenParam = "2597590896-CDibQd9rITMJCrnDSCF3Lh6HwRnb3MqxEL9uPPA";
	private static String accessTokenSecretParam ="IAk5o5yk1SS6u3HuyAP5Dk6qCLPqbcQW3wF539yIEuyBd";
	private static String screenNameParam = "isale7";
	private static String dbNameParam = "twitter_fetcher";
	
  
  @BeforeMethod
  public void beforeMethod() {
  }

  @AfterMethod
  public void afterMethod() {
  }


 
  @BeforeClass
  public void beforeClass() {
  }

  @AfterClass
  public void afterClass() {
  }

  @BeforeTest
  public void beforeTest() {
  }

  @AfterTest
  public void afterTest() {
  }

  @BeforeSuite
  public void beforeSuite() {
  }

  @AfterSuite
  public void afterSuite() {
  }


  @Test(priority=0)
  public void setupDb() {
	  //Test connection to DB
	  tweetsCollectionObj=twitterFetcherObj.setupDb(dbNameParam);
	  AssertJUnit.assertNotNull(tweetsCollectionObj);
    
  }
  
  @Test(priority=1)
  public void authentication() {
	  //Test Authentication
	  twitterObj=twitterFetcherObj.authentication(consumerKeyParam, consumerSecretParam, accessTokenParam, accessTokenSecretParam, screenNameParam, dbNameParam);
	  AssertJUnit.assertNotNull(twitterObj);
  }

  @Test(priority=2)
  public void tweetServ() {
	  //Test Twitter Fetcher Services
	  AssertJUnit.assertTrue(twitterFetcherObj.tweetServ(twitterObj, tweetsCollectionObj, screenNameParam));
	  
  }
}
