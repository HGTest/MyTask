package twitterFetcherTask.twitterFetcherTask;
import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterFetcher {
	
	
	private static DBCollection tweetsCollection;
	private static DB db;
	
	//Connect to DB service
	public  DBCollection setupDb(String dbNameParam) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient( "localhost" , 27017 );
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}

		db = mongoClient.getDB( dbNameParam );
		tweetsCollection = db.getCollection("tweets");
		return tweetsCollection;
	}
	
	//Authenticate Service
	public Twitter authentication(String consumerKeyParam,String consumerSecretParam,String accessTokenParam,String accessTokenSecretParam,String screenNameParam,String dbNameParam)
	{
		TwitterFactory tf;
		Twitter twitter;
		try
		{
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(consumerKeyParam)
		.setOAuthConsumerSecret(consumerSecretParam)
		.setOAuthAccessToken(accessTokenParam)
		.setOAuthAccessTokenSecret(accessTokenSecretParam)
		.setJSONStoreEnabled(true);
		tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		
		}
		catch (Exception e)
		{
			e.printStackTrace();		
			return null;
			
		}
		
		
		return twitter ;
	}

	//Twitter fetch services
	public boolean tweetServ(Twitter twitterObj,DBCollection tweetsCollectionObj,String screenNameParam )
	{
		List<Status> tweets = null;
		Status mostFavoured = null, mostRetweeted = null,  tweetByMostFollowers = null;
		int i = 1;
		String json;
		try {
			//when no more tweets available, will break out of the loop
			while(true)
			{
				//read the tweets in pages on a 100 tweets each
				tweets = twitterObj.getUserTimeline(screenNameParam, new Paging(i++, 100));
				
				//no more tweets to process
				if(tweets.size() == 0)
					break;
				
				
				if(mostFavoured == null)
					mostFavoured = tweets.get(0);
				if(mostRetweeted == null)
					mostRetweeted = tweets.get(0);
				
				for (Status tweet : tweets) {
					
					//parse the json and store them in the db
					json = TwitterObjectFactory.getRawJSON(tweet);
					tweetsCollectionObj.save( (DBObject) com.mongodb.util.JSON.parse(json));
					
					if(!tweet.isRetweet() && tweet.getFavoriteCount() > mostFavoured.getFavoriteCount())
						mostFavoured = tweet;
					if(!tweet.isRetweet() && tweet.getRetweetCount() > mostRetweeted.getRetweetCount())
						mostRetweeted = tweet;

					if(tweet.isRetweet() &&
							(tweetByMostFollowers == null || 
							tweet.getRetweetedStatus().getUser().getFollowersCount() > tweetByMostFollowers.getRetweetedStatus().getUser().getFollowersCount()))
						tweetByMostFollowers = tweet;
				}

			}
		} catch (TwitterException e) {
			e.printStackTrace();
			return false;
		}

		System.out.println("Total number of tweets is "+tweetsCollectionObj.count());
		System.out.println("The most favored tweet is \""+mostFavoured.getText() + "\" was favored " + mostFavoured.getFavoriteCount() + " times");
		System.out.println("The most retweeted tweet is \""+mostRetweeted.getText() + "\" was retweeted " + mostRetweeted.getRetweetCount() + " times");
		System.out.println("The tweet with most famouse author is \""+tweetByMostFollowers.getText() + "\" the auther was " 
				+ tweetByMostFollowers.getRetweetedStatus().getUser().getScreenName() + " and s/he has " + tweetByMostFollowers.getRetweetedStatus().getUser().getFollowersCount() + " followers.");

		//all done with the data, deleting the collection....
		tweetsCollectionObj.drop();
		return true;

		
	}
	
	

}