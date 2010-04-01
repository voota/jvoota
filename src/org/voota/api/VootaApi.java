package org.voota.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

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

public class VootaApi {
    public static final String strTag = "Voota";
    
    private static final int TIMEOUT = 16000;

    /*OAuth parameters*/
    private static String REQUEST_TOKEN_URL = "http://api.voota.org/oauth/request_token";
	private static String AUTHORIZATION_URL = "http://api.voota.org/oauth/authorize";
	private static String ACCESS_TOKEN_URL = "http://api.voota.org/oauth/access_token";
	
	private String m_strConsumerKey = "";
	private String m_strConsumerSecret = "";
	private String m_strCallbackUrl = "";
	
	private OAuthConsumer m_consumer = null;
	private OAuthProvider m_provider = null;
	private String m_strAccessToken = "";
	private String m_strTokenSecret = "";
	
	//private static final String m_strHostName = "http://dummy.voota.es/a1";
	private static final String m_strHostName = "http://api.voota.org/a1";
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
    
	public VootaApi(String strConsumerKey, String strConsumerSecret,
            String strCallbackUrl)
	{
        m_strConsumerKey = strConsumerKey;
        m_strConsumerSecret = strConsumerSecret;
        m_strCallbackUrl = strCallbackUrl;
        
	    m_consumer = new CommonsHttpOAuthConsumer(m_strConsumerKey, m_strConsumerSecret);

        m_provider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL,
                ACCESS_TOKEN_URL, AUTHORIZATION_URL);
	}
	

	public String getAuthorizeUrl() throws VootaApiException 
	{
	    //Intent iAuthorize = null;
        String strAuthUrl = null;
        try 
        {
            strAuthUrl = m_provider.retrieveRequestToken(m_consumer, m_strCallbackUrl);
        } 
        catch (Throwable e)
        {
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        /*catch (OAuthMessageSignerException e) 
        {
            e.printStackTrace();
        } 
        catch (OAuthNotAuthorizedException e) 
        {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

	    return strAuthUrl;
	}

	public void convertToAccessToken(String strRequestToken) throws VootaApiException 
    {
	    try 
	    {
            m_provider.retrieveAccessToken(m_consumer, strRequestToken);
            //Log.i(strTag, "Ok!!!");
            m_strAccessToken = m_consumer.getToken();
            m_strTokenSecret = m_consumer.getTokenSecret();
        } 
	    catch (Throwable e)
	    {
	        throw new VootaApiException(VootaApiException.kErrorNoAuthorize);
	    }
	    /*catch (OAuthMessageSignerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new VootaApiException(VootaApiException.kErrorNoAuthorize);
        } catch (OAuthNotAuthorizedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new VootaApiException(VootaApiException.kErrorNoAuthorize);
        } catch (OAuthExpectationFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new VootaApiException(VootaApiException.kErrorNoAuthorize);
        } catch (OAuthCommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new VootaApiException(VootaApiException.kErrorNoAuthorize);
        }*/
    }
	
	public String getAccessToken()
	{
	    return m_strAccessToken;
	}
	
	public String getTokenSecret()
	{
	    return m_strTokenSecret;
	}
	
	public ArrayList<EntityInfo> getSearchEntities(String strSearch) throws VootaApiException
	{
	    checkConnection();
	    
	    ArrayList<EntityInfo> listEntities = new ArrayList<EntityInfo>();
	    
	    try
	    {
    	    StringBuilder strUrlParams = new StringBuilder(m_strHostName);
    	    strUrlParams.append("?" + m_strPNameMethod + "=" + URLEncoder.encode(m_strPValueMethodSearch));
    	    strUrlParams.append("&" + m_strPNameSearchString + "=" + URLEncoder.encode(strSearch));
    	    
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
	    }
	    catch (Throwable e)
	    {
	        //Log.e(strTag, e.getMessage());
	    }
	    
	    return listEntities;
	}
	
	public ArrayList<EntityInfo> getTopSix() throws VootaApiException
	{
	    checkConnection();
	    
	    ArrayList<EntityInfo> listEntities = new ArrayList<EntityInfo>();
	    
	    try
	    {
    	    StringBuilder strUrlParams = new StringBuilder(m_strHostName);
    	    strUrlParams.append("?" + m_strPNameMethod + "=" + URLEncoder.encode(m_strPValueMethodTop));
    	        	    
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
	        //Log.e(strTag, e.getMessage());
	    }
	    catch (JSONException e)
	    {
	        //Log.e(strTag, e.getMessage());
	    }
	    
	    return listEntities;
	}
	

	public ArrayList<EntityInfo> getListOfPoliciesByPage (boolean bIsSortedPositive, int nPage) 
	    throws VootaApiException
	{
	    return getListOfEntitiesByPage(m_strPValuePolicies, bIsSortedPositive, nPage);
	}
	
	public ArrayList<EntityInfo> getListOfPartyByPage (boolean bIsSortedPositive, int nPage) 
	    throws VootaApiException
    {
        return getListOfEntitiesByPage(m_strPValueParty, bIsSortedPositive, nPage);
    } 
	
	static public byte[] getUrlImageBytes(URL urlImage) throws VootaApiException
	{
		byte[] bytesImage = null;
	    try 
	    {
	        HttpURLConnection conn= (HttpURLConnection)urlImage.openConnection();
	        conn.setDoInput(true);
	        conn.connect();
	        InputStream is = conn.getInputStream();
	        
	        int bytesAvavilable = conn.getContentLength();
	        bytesImage = new byte[bytesAvavilable];
	        is.read(bytesImage);
	    } 
	    catch (IOException e) 
	    {
	        throw new VootaApiException(VootaApiException.kErrorNoRespond);
	    }

	    return bytesImage;
	}

	public byte[] getUrlImageBitmap(URL urlImage)// throws VootaApiException
	{
	    byte[] bytesImage = null;
	    //Bitmap btmImage = null;
	    try 
	    {
	        HttpURLConnection conn= (HttpURLConnection)urlImage.openConnection();
	        conn.setDoInput(true);
	        conn.connect();
	        InputStream is = conn.getInputStream();

	        int bytesAvailable = conn.getContentLength();
	        bytesImage = new byte[bytesAvailable];
	        is.read(bytesImage);
	    } 
	    catch (IOException e) 
	    {
	        //throw new VootaApiException(VootaApiException.kErrorNoRespond);
	    }

	    return bytesImage;
	}
	
    private ArrayList<EntityInfo> getListOfEntitiesByPage(String strPoliciesOrParty, 
            boolean bIsSortedPositive, int nPageNumber) 
        throws VootaApiException
    {
        checkConnection();
        
        ArrayList<EntityInfo> listEntities = new ArrayList<EntityInfo>();
        
        try
        {
            StringBuilder strUrlParams = new StringBuilder(m_strHostName);
            strUrlParams.append("?" + 
                    m_strPNameMethod + "=" + URLEncoder.encode(m_strPValueMethodEntities) + "&" +
                    m_strPNameType + "=" + URLEncoder.encode(strPoliciesOrParty));
            if (!bIsSortedPositive)
            {
                strUrlParams.append("&" + m_strPNameSort + "=" + URLEncoder.encode(m_strPValueNegative));
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
            //Log.e(strTag, e.getMessage());
        }
        catch (JSONException e)
        {
            //Log.e(strTag, e.getMessage());
        }
        
        
        return listEntities;
    }
	
    public ArrayList<ReviewInfo> getReviews(EntityInfo entity) throws VootaApiException
    {
        checkConnection();
        
        ArrayList<ReviewInfo> m_listReviews = new ArrayList<ReviewInfo>();
        try
        {
            StringBuilder strUrlParams = new StringBuilder(m_strHostName);
            strUrlParams.append("?" + 
                    m_strPNameMethod + "=" + URLEncoder.encode(m_strPValueMethodReviews) + "&" +
                    m_strPNameType + "=");
            if(entity.getType() == EntityType.kPolices)
            {
                strUrlParams.append(URLEncoder.encode(m_strPValuePolicies));
            }
            else
            {
                strUrlParams.append(URLEncoder.encode(m_strPValueParty));
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
            e.printStackTrace();
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        
        return m_listReviews;
    }
    
    public void postReview(ReviewInfo newReview, String strAccessToken, 
            String strTokenSecret) throws VootaApiException
    {
    	checkConnection();
    	
    	try
    	{
    	    setAndCheckTokens(strAccessToken, strTokenSecret);
    	    
    	    m_consumer.setTokenWithSecret(m_strAccessToken, m_strTokenSecret);
            
    	    String url = OAuth.addQueryParameters(m_strHostName, URLEncoder.encode(m_strPNameMethod),
    	            URLEncoder.encode(m_strPValueMethodPostReview));
    		
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

//    		try 
//    		{
            post.setEntity(new UrlEncodedFormEntity(listParams));
            m_consumer.sign(post);
            
            HttpClient client = new DefaultHttpClient();
		    HttpResponse response = client.execute(post);
	        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
	        {
	            throw new VootaApiException(VootaApiException.kErrorCantPostReview);
	        }

            /*} 
            catch (OAuthCommunicationException e) 
            {
                e.printStackTrace();
            }
    		catch (OAuthExpectationFailedException e)
    		{
    		    e.printStackTrace();
    		}
    		catch (OAuthMessageSignerException e)
    		{
    		    e.printStackTrace();
    		}
    		catch (IllegalArgumentException e)
    		{
    		    e.printStackTrace();
    		}
    		/*HttpResponse response = client.execute(post);
    		if (response.getStatusLine().getStatusCode() != 200)
    		{
    			throw new VootaApiException(VootaApiException.kErrorCantPostReview);
    		}*/
    	}
    	catch(OAuthException e)
    	{
            throw new VootaApiException(VootaApiException.kErrorCantPostReview);
    	}
    	catch(IOException e)
    	{
    		throw new VootaApiException(VootaApiException.kErrorCantPostReview);
    	}
    }
    
    public EntityInfo getEntityInfo(EntityInfo oldEntity) throws VootaApiException
    {
        EntityInfo newEntity = null;
        
        StringBuilder builder = new StringBuilder(m_strHostName);
        builder.append("?" + m_strPNameMethod + "=" + URLEncoder.encode(m_strPValueMethodEntity));
        if (oldEntity.getType() == EntityInfo.EntityType.kParty)
        {
            builder.append("&" + m_strPNameType + "=" + URLEncoder.encode(m_strPValueParty));
        }
        else
        {
            builder.append("&" + m_strPNameType + "=" + URLEncoder.encode(m_strPValuePolicies));
        }
        builder.append("&" + m_strPNameId + "=");
        builder.append(oldEntity.getID());
        
        HttpGet get = new HttpGet(builder.toString());
        HttpClient client = new DefaultHttpClient();
        try
        {
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
            e.printStackTrace();
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            throw new VootaApiException(VootaApiException.kErrorNoRespond);
        }
        
        return newEntity;
    }
    
	private void checkConnection() throws VootaApiException
	{
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);

        HttpGet get = new HttpGet(m_strHostName);
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
	
	private void setAndCheckTokens(String strAccessToken, String strTokenSecret) throws VootaApiException
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
}