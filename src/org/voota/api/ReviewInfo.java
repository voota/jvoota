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
    
    public ReviewInfo()
    {
        m_nReviewValue = REVIEW_VALUE_POSITIVE;
        m_nID = 1;
        m_type = EntityType.kPolices;
        m_strReviewText = "";
    }
    
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
    
    public ReviewInfo(long nID, int nValue, EntityType type, String strText)
    {
    	m_nID = nID;
    	m_nReviewValue = nValue;
    	m_type = type;
    	m_strReviewText = strText;
    }
    
    public EntityType getType()
    {
        return m_type;
    }
    
    public boolean isReviewPositive()
    {
        return (m_nReviewValue == REVIEW_VALUE_POSITIVE) ? true : false ;
    }
    
    public long getID()
    {
        return m_nID;
    }
    
    public String getText()
    {
        return m_strReviewText;
    }
    
    public int getReviewValue()
    {
    	return m_nReviewValue;
    }
    
    public void setReviewText(String strText)
    {
        m_strReviewText = strText;
    }
}