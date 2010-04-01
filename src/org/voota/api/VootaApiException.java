package org.voota.api;

public class VootaApiException extends Throwable
{
    private static final long serialVersionUID = -5496834710671642889L;
    public static final int kErrorNoRespond = 100;
    public static final int kErrorNoAuthorize = 101;
    public static final int kErrorCantPostReview = 102;
    public static final int kErrorYouCantPostReview = 103;

    private final String m_strNoRespond = "Server doesn't respond!";
    private final String m_strNoAuthorize = "Not authorized by server!";
    private final String m_strCantPostReview = "There is an error while posting a review!";
    private final String m_strYouCantPostReview = "You couldn't post a review " +
    		"because you aren't authorized!";
    
    private int m_nErrorCode;
    
    public VootaApiException (int nErrorCode)
    {
       m_nErrorCode = nErrorCode; 
    }
    
    public String getMessage()
    {
        String strError = "";
        
        switch(m_nErrorCode)
        {
        case kErrorNoRespond:
            strError = m_strNoRespond;
            break;
        case kErrorNoAuthorize:
            strError = m_strNoAuthorize;
            break;
        case kErrorCantPostReview:
        	strError = m_strCantPostReview;
        	break;
        case kErrorYouCantPostReview:
            strError = m_strYouCantPostReview;
            break;
        }
        
        return strError;
    }
}