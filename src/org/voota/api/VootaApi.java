/*
 * This file is part of the Voota package.
 * (c) 2010 Tatyana Ulyanova <levkatata.voota@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

/**
 * This file contains implementation of VootaApi class. This class includes 
 * implementation of all client-side methods provided by Voota REST Api 
 * for getting/posting information from/to Voota server. 
 * Also there are OAuth authorization client-side method implementations.
 *
 * @package    Voota
 * @subpackage Api
 * @author     Tatyana Ulyanova
 * @version    1.0
 */

package org.voota.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.voota.api.EntityInfo.EntityType;

public class VootaApi implements Serializable {
    public static final String strTag = "Voota";
    
    private static final long serialVersionUID = 5855835290453961251L;
    private static final int TIMEOUT = 16000;
    private static final String CHARSET = "UTF-8";

    /*OAuth parameters*/
	private static final String HOSTNAME_PROD = "http://voota.es";
    
	private static final String API_POSTFIX = "/a1";
    private static final String REQUEST_TOKEN_POSTFIX = "/oauth/request_token";
	private static final String AUTHORIZATION_POSTFIX = "/oauth/authorize";
	private static final String ACCESS_TOKEN_POSTFIX = "/oauth/access_token";
	
	private static String m_strUsedHost = "";
	private static String REQUEST_TOKEN_URL = "";
	private static String AUTHORIZATION_URL = "";
	private static String ACCESS_TOKEN_URL= "";
	
	private String m_strConsumerKey = "";
	private String m_strConsumerSecret = "";
	private String m_strCallbackUrl = "";
	
	private OAuthConsumer m_consumer = null;
	private OAuthProvider m_provider = null;
	private String m_strAccessToken = "";
	private String m_strTokenSecret = "";
	
	/*Server's methods*/
	private static final String m_strPNameMethod = "method";
	
	private static final String m_strPValueMethodSearch = "search";
	private static final String m_strPNameSearchString = "q";
	
	private static final String m_strPValueMethodTop = "top";
	
	private static final String m_strPValueMethodEntities = "entities";
	private static final String m_strPNameType = "type";
	private static final String m_strPNameValue = "value";
	private static final String m_strPValuePolicies = "politician";
	private static final String m_strPValueParty = "party";
    private static final String m_strPNameSort = "sort";
    private static final String m_strPValuePositive = "positive";
    private static final String m_strPValueNegative = "negative";
    private static final String m_strPNamePage = "page";

    private static final String m_strPValueMethodReviews = "reviews";
    private static final String m_strPNameEntity = "entity";

    private static final String m_strPValueMethodEntity = "entity";
    private static final String m_strPNameId = "id";
   
    private static final String m_strPValueMethodPostReview = "review";
    private static final String m_strPNameText = "text";
    private static final String m_strPValuePosReview = "1";
    private static final String m_strPValueNegReview = "-1";
    public static final int m_nPageSize = 20;
    
    /**
     * Constructor of class VootaApi creates object for working with Voota REST API 
     * methods and OAuth authorization. This object uses default server host name.  
     *
     * @param strConsumerKey    OAuth consumer key
     * @param strConsumerSecret OAuth consumer secret
     * @param strCallbackUrl    callback url to return to application after 
     *                          authorization was done
     */
	public VootaApi(String strConsumerKey, String strConsumerSecret,
            String strCallbackUrl)
	{
        m_strConsumerKey = strConsumerKey;
        m_strConsumerSecret = strConsumerSecret;
        m_strCallbackUrl = strCallbackUrl;
        
        m_strUsedHost = HOSTNAME_PROD + API_POSTFIX;
        REQUEST_TOKEN_URL = HOSTNAME_PROD + REQUEST_TOKEN_POSTFIX;
        ACCESS_TOKEN_URL = HOSTNAME_PROD + ACCESS_TOKEN_POSTFIX;
        AUTHORIZATION_URL = HOSTNAME_PROD + AUTHORIZATION_POSTFIX;
        
	    m_consumer = new CommonsHttpOAuthConsumer(m_strConsumerKey, m_strConsumerSecret);

        m_provider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL,
                ACCESS_TOKEN_URL, AUTHORIZATION_URL);
	}
	
    /**
     * Constructor of class VootaApi creates object for working with Voota REST API 
     * methods and OAuth authorization. This object uses custom sever host name passed 
     * in corresponding parameter.
     *
     * @param strConsumerKey    OAuth consumer key
     * @param strConsumerSecret OAuth consumer secret
     * @param strCallbackUrl    callback url to return to application after 
     *                          authorization was done
     * @param strCustomHostName host name will be used as Voota REST API server name 
     */
	public VootaApi(String strConsumerKey, String strConsumerSecret,
            String strCallbackUrl, String strCustomHostName)
	{
        m_strConsumerKey = strConsumerKey;
        m_strConsumerSecret = strConsumerSecret;
        m_strCallbackUrl = strCallbackUrl;
        
        m_strUsedHost = strCustomHostName + API_POSTFIX;
        REQUEST_TOKEN_URL = strCustomHostName + REQUEST_TOKEN_POSTFIX;
        ACCESS_TOKEN_URL = strCustomHostName + ACCESS_TOKEN_POSTFIX;
        AUTHORIZATION_URL = strCustomHostName + AUTHORIZATION_POSTFIX;
        
        m_consumer = new CommonsHttpOAuthConsumer(m_strConsumerKey, m_strConsumerSecret);

        m_provider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL,
                ACCESS_TOKEN_URL, AUTHORIZATION_URL);
	}

    /**
     * Returns OAuth authorization url in String object will be used for opening
     * authorization page in browser.
     *
     * @return                   OAuth authorization url
     * @throws VootaApiException if method fails to retrieve OAuth authorization url
     * @see org.voota.api.VootaApiException VootaApiException
     */
	public String getAuthorizeUrl() throws VootaApiException 
	{
        String strAuthUrl = null;
        
        try 
        {
            strAuthUrl = m_provider.retrieveRequestToken(m_consumer, m_strCallbackUrl);
        } 
        catch (Throwable e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoAuthorize);
        }

	    return strAuthUrl;
	}

    /**
     * Gets access token and token secret from consumer used request token returned 
     * from authorization page to application. This data is saved in corresponding
     * variables inside of VootaApi object and will be used to post authorization
     * data to server.
     *
     * @param  strRequestToken   OAuth request token was returned by consumer
     * @throws VootaApiException if method fails to retrieve access token and token
     *                           secret
     * @see org.voota.api.VootaApiException VootaApiException
     */
	public void convertToAccessToken(String strRequestToken) throws VootaApiException 
    {
	    try 
	    {
            m_provider.retrieveAccessToken(m_consumer, strRequestToken);
            m_strAccessToken = m_consumer.getToken();
            m_strTokenSecret = m_consumer.getTokenSecret();
        } 
	    catch (Throwable e)
	    {
	        throw new VootaApiException(VootaApiException.kErrorNoAuthorize);
	    }
    }
	
    /**
     * Returns OAuth access token retrieved from server earlier.
     *
     * @return  String object represents OAuth access token
     */
	public String getAccessToken()
	{
	    return m_strAccessToken;
	}
	
    /**
     * Returns OAuth token secret retrieved from server earlier.
     *
     * @return  String object represents OAuth token secret
     */
	public String getTokenSecret()
	{
	    return m_strTokenSecret;
	}
	
    /**
     * Returns list of entities got from server as search result by keyword.
     *
     * @param  strSearch         keyword for searching
     * @return                   ArrayList of EntityInfo objects represent search
     *                           results
     * @throws VootaApiException if method fails to retrieve search results from
     *                           server
     * @see <a href="http://trac.voota.org/wiki/search">Search Voota API method</a>
     * @see org.voota.api.VootaApiException VootaApiException
     * @see org.voota.api.EntityInfo        EntityInfo
     */
	public ArrayList<EntityInfo> getSearchEntities(String strSearch) 
	    throws VootaApiException
	{
	    checkConnection();
	    
	    ArrayList<EntityInfo> listEntities = new ArrayList<EntityInfo>();
	    
	    try
	    {
    	    StringBuilder strUrlParams = new StringBuilder(m_strUsedHost);
    	    strUrlParams.append("?" + m_strPNameMethod + "=" + 
    	            URLEncoder.encode(m_strPValueMethodSearch, CHARSET));
    	    strUrlParams.append("&" + m_strPNameSearchString + "=" + 
    	            URLEncoder.encode(strSearch, CHARSET));
    	    
    	    HttpClient client = new DefaultHttpClient();
    	    HttpGet get = new HttpGet(strUrlParams.toString());
    	    HttpResponse response = client.execute(get);
    	    
    	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    	    {
        	    String strResponceBody = convertIStreamToString(response.getEntity().getContent());
        	    JSONArray arrayEntities = new JSONArray(strResponceBody);
        	    int nArraySize = arrayEntities.length(), i = 0;
        	    for (; i < nArraySize; i++)
        	    {
        	        listEntities.add(new EntityInfo(arrayEntities.getJSONObject(i)));
        	    }
    	    }
    	    else
    	    {
    	        throw new VootaApiException(VootaApiException.kErrorNoRespond);
    	    }
	    }
	    catch (IOException e)
	    {
	        throw new VootaApiException(VootaApiException.kErrorNoRespond);
	    }
	    catch (JSONException e)
	    {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
	    }
	    
	    return listEntities;
	}
	
    /**
     * Returns list of entities got from server as top entities.
     *
     * @return                   ArrayList of EntityInfo objects represent top
     *                           entities
     * @throws VootaApiException if method fails to retrieve top entities from
     *                           server
     * @see <a href="http://trac.voota.org/wiki/top">Top Voota API method</a>
     * @see org.voota.api.VootaApiException VootaApiException
     * @see org.voota.api.EntityInfo        EntityInfo
     */
	public ArrayList<EntityInfo> getTop() throws VootaApiException
	{
	    checkConnection();
	    
	    ArrayList<EntityInfo> listEntities = new ArrayList<EntityInfo>();
	    
	    try
	    {
    	    StringBuilder strUrlParams = new StringBuilder(m_strUsedHost);
    	    strUrlParams.append("?" + m_strPNameMethod + "=" + 
    	            URLEncoder.encode(m_strPValueMethodTop, CHARSET));
    	        	    
    	    DefaultHttpClient client = new DefaultHttpClient();
    	    HttpGet get = new HttpGet(strUrlParams.toString());
    	    HttpResponse response = client.execute(get);
 
    	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    	    {
        	    String strResponceBody = convertIStreamToString(response.getEntity().getContent());
        	    JSONArray arrayEntities = new JSONArray(strResponceBody);
        	    int arraySize = arrayEntities.length(), i = 0;
        	    
        	    for (; i < arraySize; i++)
        	    {
                    listEntities.add(new EntityInfo(arrayEntities.getJSONObject(i)));
                }
    	    }
    	    else
    	    {
    	        throw new VootaApiException(VootaApiException.kErrorNoRespond);
    	    }
	    }
	    catch (IOException e)
	    {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
	    }
	    catch (JSONException e)
	    {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
	    }
	    
	    return listEntities;
	}
	

    /**
     * Returns list of politicians by defined number of page got from server. Sort
     * order may be either by positive votes or negative votes and specifies in 
     * parameter.
     *
     * @param  bIsSortedPositive variable shows how politicians should be
     *                           sorted: true - by positive votes, false - by
     *                           negative votes
     * @param  nPage             number of page list to retrieve from server
     * @return                   ArrayList of EntityInfo objects represent 
     *                           politicians
     * @throws VootaApiException if method fails to retrieve politicians from
     *                           server
     * @see <a href="http://trac.voota.org/wiki/entities">Entities Voota API method</a>
     * @see org.voota.api.VootaApiException VootaApiException
     * @see org.voota.api.EntityInfo        EntityInfo
     */
	public ArrayList<EntityInfo> getListOfPoliciesByPage (boolean bIsSortedPositive, int nPage) 
	    throws VootaApiException
	{
	    return getListOfEntitiesByPage(m_strPValuePolicies, bIsSortedPositive, nPage);
	}
	
    /**
     * Returns list of parties by defined number of page got from server. Sort
     * order may be either by positive votes or negative votes and specifies in 
     * parameter.
     *
     * @param  bIsSortedPositive variable shows how parties should be
     *                           sorted: true - by positive votes, false - by
     *                           negative votes
     * @param  nPage             number of page list to retrieve from server
     * @return                   ArrayList of EntityInfo objects represent 
     *                           parties
     * @throws VootaApiException if method fails to retrieve parties from
     *                           server
     * @see <a href="http://trac.voota.org/wiki/entities">Entities Voota API method</a>
     * @see org.voota.api.VootaApiException VootaApiException
     * @see org.voota.api.EntityInfo        EntityInfo
     */
	public ArrayList<EntityInfo> getListOfPartyByPage (boolean bIsSortedPositive, int nPage) 
	    throws VootaApiException
    {
        return getListOfEntitiesByPage(m_strPValueParty, bIsSortedPositive, nPage);
    } 
	
    /**
     * Loads image data by url and returns byte array filled by this data. If
     * method fails to load image, it will return null.
     *
     * @param  urlImage          url represents image to load
     * @return                   byte array filled by image data or null if image
     *                           loading fails
     */
	static public byte[] getUrlImageBytes(URL urlImage)
	{
		byte[] bytesImage = null;
	    try 
	    {
	        HttpURLConnection conn = (HttpURLConnection)urlImage.openConnection();
	        conn.setDoInput(true);
	        conn.connect();
	        InputStream is = conn.getInputStream();
	        
	        int bytesAvavilable = conn.getContentLength();
	        bytesImage = new byte[bytesAvavilable];
            int nReaded = 0, nSum = 0;
            
            while (bytesAvavilable > nSum)
            {
                nReaded = is.read(bytesImage, nSum, bytesAvavilable - nSum);
                nSum += nReaded; 
            }
	    } 
	    catch (IOException e) 
	    {
	    }

	    return bytesImage;
	}
	
    /**
     * Loads image data by url and returns byte array filled by this data. If
     * method fails to load image, it will return null. This method takes url as
     * String object and converts file name to used character set, because it
     * may contain Spanish symbols. 
     *
     * @param  urlImage          url in String object represents image to load
     * @return                   byte array filled by image data or null if image
     *                           loading fails
     */
    static public byte[] getUrlImageBytes(String strUrlImage)
    {
        byte[] bytesImage = null;
        try 
        {
            URL urlImage = new URL(getEncodedImageUrl(strUrlImage));
            HttpURLConnection conn= (HttpURLConnection)urlImage.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            
            int bytesAvavilable = conn.getContentLength();
            bytesImage = new byte[bytesAvavilable];
            int nReaded = 0, nSum = 0;
            
            while (bytesAvavilable > nSum)
            {
                nReaded = is.read(bytesImage, nSum, bytesAvavilable - nSum);
                nSum += nReaded; 
            }
        } 
        catch (IOException e) 
        {
        }

        return bytesImage;
    }

    /**
     * Returns list of entities by defined number of page got from server. Sort
     * order may be either by positive votes or negative votes and specifies in 
     * parameter. Method is used by other Voota Api methods.
     *
     * @param  strPoliticOrParty type of entity to get
     * @param  bIsSortedPositive variable shows how entities should be
     *                           sorted: true - by positive votes, false - by
     *                           negative votes
     * @param  nPage             number of page list to retrieve from server
     * @return                   ArrayList of EntityInfo objects represent 
     *                           entities
     * @throws VootaApiException if method fails to retrieve entities from
     *                           server
     * @see <a href="http://trac.voota.org/wiki/entities">Entities Voota API method</a>
     * @see org.voota.api.VootaApiException VootaApiException
     * @see org.voota.api.EntityInfo        EntityInfo
     */
    private ArrayList<EntityInfo> getListOfEntitiesByPage(String strPoliticOrParty, 
            boolean bIsSortedPositive, int nPageNumber) 
        throws VootaApiException
    {
        checkConnection();
        
        ArrayList<EntityInfo> listEntities = new ArrayList<EntityInfo>();
        
        try
        {
            StringBuilder strUrlParams = new StringBuilder(m_strUsedHost);
            strUrlParams.append("?" + m_strPNameMethod + "=" + 
                    URLEncoder.encode(m_strPValueMethodEntities, CHARSET) 
                    + "&" + m_strPNameType + "=" + 
                    URLEncoder.encode(strPoliticOrParty, CHARSET));
            if (!bIsSortedPositive)
            {
                strUrlParams.append("&" + m_strPNameSort + "=" + 
                        URLEncoder.encode(m_strPValueNegative, CHARSET));
            }
            if (nPageNumber != 0)
            {
                strUrlParams.append("&" + m_strPNamePage + "=" + String.valueOf(nPageNumber));
            }
            
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(strUrlParams.toString());
            HttpResponse response = client.execute(get);
 
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                String strResponceBody = convertIStreamToString(response.getEntity().getContent());
                JSONArray arrayEntities = new JSONArray(strResponceBody);
                int arraySize = arrayEntities.length(), i = 0;
                
                for (; i < arraySize; i++)
                {
                    listEntities.add(new EntityInfo(arrayEntities.getJSONObject(i)));
                }
            }
            else
            {
                throw new VootaApiException(VootaApiException.kErrorNoRespond);
            }
        }
        catch (IOException e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        catch (JSONException e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        
        return listEntities;
    }
	
    /**
     * Returns list of reviews by defined entity got from server.
     *
     * @param  entity            information about entity to get reviews by one
     * @return                   ArrayList of ReviewInfo objects represent 
     *                           reviews by given entity
     * @throws VootaApiException if method fails to retrieve list of reviews from
     *                           server
     * @see <a href="http://trac.voota.org/wiki/reviews">Reviews Voota API method</a>
     * @see org.voota.api.VootaApiException VootaApiException
     * @see org.voota.api.ReviewInfo        ReviewInfo
     */
    public ArrayList<ReviewInfo> getReviews(EntityInfo entity) 
        throws VootaApiException
    {
        checkConnection();
        
        ArrayList<ReviewInfo> m_listReviews = new ArrayList<ReviewInfo>();
        try
        {
            StringBuilder strUrlParams = new StringBuilder(m_strUsedHost);
            strUrlParams.append("?" + m_strPNameMethod + "=" + 
                    URLEncoder.encode(m_strPValueMethodReviews, CHARSET) + "&" +
                    m_strPNameType + "=");
            if(entity.getType() == EntityType.kPolices)
            {
                strUrlParams.append(URLEncoder.encode(m_strPValuePolicies, CHARSET));
            }
            else
            {
                strUrlParams.append(URLEncoder.encode(m_strPValueParty, CHARSET));
            }
            strUrlParams.append("&" + m_strPNameEntity + "=" + String.valueOf(entity.getID()));
            
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(strUrlParams.toString());
            HttpResponse response = client.execute(get);
            
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                String strReviews = convertIStreamToString(response.getEntity().getContent());
                JSONArray arrayReviews = new JSONArray(strReviews);
                int nLeigth = arrayReviews.length();
                for (int i = 0; i < nLeigth; i++)
                {
                    m_listReviews.add(new ReviewInfo(arrayReviews.getJSONObject(i)));
                }
            }
            else
            {
                throw new VootaApiException(VootaApiException.kErrorNoRespond);
            }
        }
        catch(IOException e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        catch(JSONException e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        
        return m_listReviews;
    }
    
    /**
     * Post a review to server. This method uses OAuth authorization data to post.
     *
     * @param  newReview         information about new review to post 
     * @param  strAccessToken    OAuth access token
     * @param  strTokenSecret    OAuth token secret 
     * @throws VootaApiException if method fails to post a review to server
     * @see <a href="http://trac.voota.org/wiki/post_review">Review Voota API method</a>
     * @see org.voota.api.VootaApiException VootaApiException
     * @see org.voota.api.ReviewInfo        ReviewInfo
     */
    public void postReview(ReviewInfo newReview, String strAccessToken, 
            String strTokenSecret) throws VootaApiException
    {
    	checkConnection();
    	
    	try
    	{
    	    setAndCheckTokens(strAccessToken, strTokenSecret);
    	    
    	    m_consumer.setTokenWithSecret(m_strAccessToken, m_strTokenSecret);
            
    	    String url = OAuth.addQueryParameters(m_strUsedHost, 
    	            URLEncoder.encode(m_strPNameMethod, CHARSET),
    	            URLEncoder.encode(m_strPValueMethodPostReview, CHARSET));
    		
    		HttpPost post = new HttpPost(url);
    		
    		List<NameValuePair> listParams = new ArrayList<NameValuePair>();
            if(newReview.getType() == EntityType.kPolices)
            {
                listParams.add(new BasicNameValuePair(m_strPNameType, m_strPValuePolicies));
            }
            else
            {
                listParams.add(new BasicNameValuePair(m_strPNameType, m_strPValueParty));
            }
            listParams.add(new BasicNameValuePair(m_strPNameEntity, Long.toString(newReview.getID())));
            listParams.add(new BasicNameValuePair(m_strPNameValue, Integer.toString(newReview.getReviewValue())));
            if(newReview.getText().length() != 0)
            {
                listParams.add(new BasicNameValuePair(m_strPNameText, newReview.getText()));
            }

            post.setEntity(new UrlEncodedFormEntity(listParams));
            m_consumer.sign(post);
            
            HttpClient client = new DefaultHttpClient();
		    HttpResponse response = client.execute(post);
	        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
	        {
	            throw new VootaApiException(VootaApiException.kErrorReviewNotPosted);
	        }
    	}
    	catch(OAuthException e)
    	{
            throw new VootaApiException(VootaApiException.kErrorReviewNotPosted);
    	}
    	catch(IOException e)
    	{
    		throw new VootaApiException(VootaApiException.kErrorReviewNotPosted);
    	}
    }
    
    /**
     * Returns information about entity got from server.
     *
     * @param  oldEntity         information about entity to get (uses only ID 
     *                           and type) 
     * @return                   EntityInfo object represents one entity
     * @throws VootaApiException if method fails to retrieve list of reviews from
     *                           server
     * @see <a href="http://trac.voota.org/wiki/entity">Entity Voota API method</a>
     * @see org.voota.api.VootaApiException VootaApiException
     * @see org.voota.api.EntityInfo        EntityInfo
     */
    public EntityInfo getEntityInfo(EntityInfo oldEntity) throws VootaApiException
    {
        checkConnection();
        EntityInfo newEntity = null;
  
        try
        {
            StringBuilder builder = new StringBuilder(m_strUsedHost);
            builder.append("?" + m_strPNameMethod + "=" + 
                    URLEncoder.encode(m_strPValueMethodEntity, CHARSET));
            if (oldEntity.getType() == EntityInfo.EntityType.kParty)
            {
                builder.append("&" + m_strPNameType + "=" + 
                        URLEncoder.encode(m_strPValueParty, CHARSET));
            }
            else
            {
                builder.append("&" + m_strPNameType + "=" + 
                        URLEncoder.encode(m_strPValuePolicies, CHARSET));
            }
            builder.append("&" + m_strPNameId + "=");
            builder.append(oldEntity.getID());
            
            HttpGet get = new HttpGet(builder.toString());
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                String strEntity = convertIStreamToString(response.getEntity().getContent());
                newEntity = new EntityInfo(new JSONObject(strEntity));
            }
            else
            {
                throw new VootaApiException(VootaApiException.kErrorNoRespond);
            }
        }
        catch(IOException e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        catch(JSONException e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        
        return newEntity;
    }
    
    /**
     * Check connection with server.
     *
     * @throws VootaApiException if connection can't be establish
     * @see org.voota.api.VootaApiException VootaApiException
     */
	private void checkConnection() throws VootaApiException
	{
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);

        HttpGet get = new HttpGet(m_strUsedHost);
        get.setParams(params);
        HttpClient httpClient = new DefaultHttpClient();
        
        try
        {
            httpClient.execute(get);
        }
        catch (Throwable e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
    }
	
    /**
     * Sets and checks access token and token secret got from application. Method
     * is used by other VootaApi method to verify if user is authorized.
     *
     * @param  strAccessToken    OAuth access token
     * @param  strTokenSecret    OAuth token secret 
     * @throws VootaApiException if OAuth information is empty
     * @see org.voota.api.VootaApiException VootaApiException
     */
	private void setAndCheckTokens(String strAccessToken, String strTokenSecret) 
	    throws VootaApiException
	{
	    if (m_strAccessToken.length() == 0)
        {
            m_strAccessToken = strAccessToken;
        }
        if (m_strTokenSecret.length() == 0)
        {
            m_strTokenSecret = strTokenSecret;
        }
        
        if (m_strAccessToken == null || m_strTokenSecret == null)
        {
            throw new VootaApiException(VootaApiException.kErrorYouCantPostReview);
        }
	    if (m_strAccessToken.length() == 0 || m_strTokenSecret.length() == 0)
	    {
            throw new VootaApiException(VootaApiException.kErrorYouCantPostReview);
	    }
	}
	
    /**
     * Convert InputStream object to String object. Method is used by other VootaApi
     * methods to convert content of server reply to String.
     *
     * @param  istream    object to convert
     * @return            String object contains the same content as parameter
     */
	private String convertIStreamToString(InputStream istream)
	{
	    BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
	    StringBuilder strBuilder = new StringBuilder();
	    
	    char[] chBuffer = new char[1024];
	    int nActualBytes = 0;
	    try
	    {
	        while ((nActualBytes = reader.read(chBuffer)) != -1)
	        {
	            strBuilder.append(chBuffer, 0, nActualBytes);
	        }
	    }
	    catch (Throwable e)
	    {
	    }
	    finally
	    {
            try
            {
                istream.close();
            } 
            catch (IOException e) 
            {
            }
	    }
	    
	    return strBuilder.toString();
	}
	
    /**
     * Encode image file name in strImageUrl to used character set. 
     *
     * @param  strImageUrl image url to encode
     * @return             encoded url
     */
	static private String getEncodedImageUrl(String strImageUrl)
	{  
	    String strResult = strImageUrl;
	    try
	    {
    	    int nLastIndex = strImageUrl.lastIndexOf("/");
    	    if (nLastIndex != -1)
    	    {
        	    strResult = strImageUrl.substring(0, nLastIndex + 1); 
        	    strResult += URLEncoder.encode(strImageUrl.substring(nLastIndex + 1), 
        	            CHARSET);
    	    }
	    }
	    catch (UnsupportedEncodingException e)
	    {
	        strResult = strImageUrl;
	    }
        return strResult;
	}
}
