/*
 * This file is part of the Voota package.
 * (c) 2010 Tatyana Ulyanova <levkatata.voota@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

/**
 * This file contains implementation of VootaApiException class. This class 
 * is used to store information about errors could occur while interaction with
 * Voota server.
 *
 * @package    Voota
 * @subpackage Api
 * @author     Tatyana Ulyanova
 * @version    1.0
 */

package org.voota.api;

public class VootaApiException extends Throwable
{
    private static final long serialVersionUID = -5496834710671642889L;
    public static final int kErrorNoRespond = 100;
    public static final int kErrorNoAuthorize = 101;
    public static final int kErrorReviewNotPosted = 102;
    public static final int kErrorYouCantPostReview = 103;

    private final String m_strNoRespond = "Server doesn't respond!";
    private final String m_strNoAuthorize = "Not authorized by server!";
    private final String m_strReviewNotPosted = "There is an error while posting a review!";
    private final String m_strYouCantPostReview = "You couldn't post a review " +
    		"because you aren't authorized!";
    
    private int m_nErrorCode;
    private String m_strErrorMessage;
    
    public VootaApiException (int nErrorCode)
    {
       m_nErrorCode = nErrorCode;
       m_strErrorMessage = "";
    }
    
    public VootaApiException (int nErrorCode, String strMessage)
    {
        m_nErrorCode = nErrorCode;
        m_strErrorMessage = strMessage;
    }
    
    @Deprecated
    public String getMessage()
    {
        String strError = m_strErrorMessage;
        if (strError.length() != 0)
        {
            return strError;
        }
        
        switch(m_nErrorCode)
        {
        case kErrorNoRespond:
            strError = m_strNoRespond;
            break;
        case kErrorNoAuthorize:
            strError = m_strNoAuthorize;
            break;
        case kErrorReviewNotPosted:
        	strError = m_strReviewNotPosted;
        	break;
        case kErrorYouCantPostReview:
            strError = m_strYouCantPostReview;
            break;
        }
        
        return strError;
    }
    
    public int getErrorCode()
    {
        return m_nErrorCode;
    }
}