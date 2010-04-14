/*
 * This file is part of the Voota package.
 * (c) 2010 Tatyana Ulyanova <levkatata.voota@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

/**
 * This file contains implementation of ReviewInfo class. This class includes all
 * information about one review.
 *
 * @package    Voota
 * @subpackage Api
 * @author     Tatyana Ulyanova
 * @version    1.0
 */

package org.voota.api;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.voota.api.EntityInfo.EntityType;

public class ReviewInfo implements Serializable
{
    private static final long serialVersionUID = -4694812022315054417L;
    private static final String JSON_PARAM_VALUE = "value";
    private static final String JSON_PARAM_ID = "id";
    private static final String JSON_PARAM_TYPE = "type";
    private static final String JSON_PARAM_TEXT = "text";
    public static final int REVIEW_VALUE_POSITIVE = 1;
    public static final int REVIEW_VALUE_NEGATIVE = -1;
    
    private int m_nReviewValue;
    private long m_nID;
    private EntityType m_type;
    private String m_strReviewText;
    
    /**
     * Default constructor of class ReviewInfo that creates empty review object.
     */
    public ReviewInfo()
    {
        m_nReviewValue = REVIEW_VALUE_POSITIVE;
        m_nID = 1;
        m_type = EntityType.kPolices;
        m_strReviewText = "";
    }
    
    /**
     * Constructor of class ReviewInfo that creates object based on 
     * appropriate JSONObject data. Constructor parses json object and extracts
     * each field by key.
     *
     * @param  jsonReview    object in json format contained appropriate review fields
     * @throws JSONException if JSONObject parameter doesn't contain some field
     * @see <a href="http://www.json.org/javadoc/org/json/JSONObject.html">JSONObject</a>
     * @see <a href="http://trac.voota.org/wiki/review_element">Review element</a>
     */
    public ReviewInfo(JSONObject jsonReview) throws JSONException
    {
        String strType = jsonReview.getString(JSON_PARAM_TYPE);
        if(strType.equals(EntityInfo.JSON_VALUE_POLICIES))
        {
            m_type = EntityType.kPolices;
        }
        else
        {
            m_type = EntityType.kParty;
        }

        m_nReviewValue = jsonReview.getInt(JSON_PARAM_VALUE);
        m_nID = jsonReview.getInt(JSON_PARAM_ID);
        m_strReviewText = jsonReview.isNull(JSON_PARAM_TEXT) ? 
                "" : jsonReview.getString(JSON_PARAM_TEXT);
    }
    
    /**
     * Constructor of class ReviewInfo that creates object based on parameters. 
     *
     * @param nID     a unique identifier of the review within a type
     * @param nValue  positive (1) or negative (-1) review
     * @param type    entity type was reviewed 
     * @param strText text of review
     * @see <a href="http://trac.voota.org/wiki/review_element">Review element</a>
     */
    public ReviewInfo(long nID, int nValue, EntityType type, String strText)
    {
    	m_nID = nID;
    	m_nReviewValue = nValue;
    	m_type = type;
    	m_strReviewText = strText;
    }
    
    /**
     * Returns entity type was reviewed. 
     *
     * @returns entity type
     * @see <a href="http://trac.voota.org/wiki/review_element">Review element</a>
     */
    public EntityType getType()
    {
        return m_type;
    }
    
    /**
     * Returns boolean value indicates if review is positive. 
     *
     * @returns true if review is positive, false - otherwise
     * @see <a href="http://trac.voota.org/wiki/review_element">Review element</a>
     */
    public boolean isReviewPositive()
    {
        return (m_nReviewValue == REVIEW_VALUE_POSITIVE) ? true : false ;
    }
    
    /**
     * Returns a unique identifier of the review within a type. 
     *
     * @returns review ID
     * @see <a href="http://trac.voota.org/wiki/review_element">Review element</a>
     */
    public long getID()
    {
        return m_nID;
    }
    
    /**
     * Returns review text. 
     *
     * @returns review text
     * @see <a href="http://trac.voota.org/wiki/review_element">Review element</a>
     */
    public String getText()
    {
        return m_strReviewText;
    }
    
    /**
     * Returns review value: 1 - positive review, -1 - negative review. 
     *
     * @returns review value
     * @see <a href="http://trac.voota.org/wiki/review_element">Review element</a>
     */
    public int getReviewValue()
    {
    	return m_nReviewValue;
    }
    
    /**
     * Set review text. 
     *
     * @param strText review text
     * @see <a href="http://trac.voota.org/wiki/review_element">Review element</a>
     */
    public void setReviewText(String strText)
    {
        m_strReviewText = strText;
    }
}