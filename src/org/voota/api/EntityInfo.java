package org.voota.api;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class EntityInfo implements Serializable
{
    private static final long serialVersionUID = 3035971415259575089L;
    private static final String JSON_PARAM_ID = "id";
    private static final String JSON_PARAM_NAME = "name";
    private static final String JSON_PARAM_LNAME = "longName";
    private static final String JSON_PARAM_IMAGE = "image";
    private static final String JSON_PARAM_IMAGE_BW = "image_bw";
    private static final String JSON_PARAM_IMAGE_S = "image_s";
    private static final String JSON_PARAM_IMAGE_S_BW = "image_s_bw";
    private static final String JSON_PARAM_TYPE = "type";
    private static final String JSON_PARAM_POS_VOTES = "positives";
    private static final String JSON_PARAM_REC_POS_VOTES = "recentPositives";
    private static final String JSON_PARAM_NEG_VOTES = "negatives";
    private static final String JSON_PARAM_REC_NEG_VOTES = "recentNegatives";
    private static final String JSON_PARAM_DESCRIPTION = "description";
    
    public static final String JSON_VALUE_POLICIES = "politico";
    public static final String JSON_VALUE_PARTY = "partido";
    
    public enum EntityType
    {
        kPolices,
        kParty
    };
    
    private long m_lID;
    private EntityType m_type;
    private String m_strName;
    private String m_strLongName;
    private String m_strImage;
    private String m_strImageBW;
    private String m_strImageS;
    private byte[] m_bImageSmall;
    private String m_strImageSBW;
    private int m_nPositiveVotes;
    private int m_nRecPositiveVotes;
    private int m_nNegativeVotes;
    private int m_nRecNegativeVotes;
    private String m_strDescription;
    
    public EntityInfo(JSONObject jsonEntity) throws JSONException
    {
        String strType = jsonEntity.getString(JSON_PARAM_TYPE);  
        if (strType.equals(JSON_VALUE_POLICIES))
        {
            m_type = EntityType.kPolices;
        }
        else
        {
            m_type = EntityType.kParty;
        }
        m_lID = jsonEntity.getLong(JSON_PARAM_ID);
        m_strName = jsonEntity.getString(JSON_PARAM_NAME);
        m_strLongName = jsonEntity.getString(JSON_PARAM_LNAME);
        m_strImage = jsonEntity.getString(JSON_PARAM_IMAGE);
        m_strImageBW = jsonEntity.getString(JSON_PARAM_IMAGE_BW);
        m_strImageS = jsonEntity.getString(JSON_PARAM_IMAGE_S);
        m_strImageSBW = jsonEntity.getString(JSON_PARAM_IMAGE_S_BW);
        m_nPositiveVotes = jsonEntity.getInt(JSON_PARAM_POS_VOTES);
        m_nRecPositiveVotes = jsonEntity.getInt(JSON_PARAM_REC_POS_VOTES);
        m_nNegativeVotes = jsonEntity.getInt(JSON_PARAM_NEG_VOTES);
        m_nRecNegativeVotes = jsonEntity.getInt(JSON_PARAM_REC_NEG_VOTES);
        m_strDescription = jsonEntity.getString(JSON_PARAM_DESCRIPTION);
        
        try
        {
            m_bImageSmall = VootaApi.getUrlImageBytes(m_strImageS);
        }
        catch (VootaApiException e)
        {
            
        }
    }
    
    public final long getID()
    {
        return m_lID;
    }
    
    public final EntityType getType()
    {
        return m_type;
    }
    
    public final String getName()
    {
        return m_strName;
    }
    
    public final String getLongName()
    {
        return m_strLongName;
    }
    
    public final String getUrlImage()
    {
        return m_strImage;
    }
    
    public final String getUrlImageBW()
    {
        return m_strImageBW;
    }
    
    public final String getUrlImageSmall()
    {
        return m_strImageS;
    }
    
    public final String getUrlImageBWSmall()
    {
        return m_strImageSBW;
    }
    
    public final int getPositiveVotes()
    {
        return m_nPositiveVotes;
    }
    
    public final int getRecPositiveVotes()
    {
        return m_nRecPositiveVotes;
    }
    
    public final int getNegativeVotes()
    {
        return m_nNegativeVotes;
    }
    
    public final int getRecNegativeVotes()
    {
        return m_nRecNegativeVotes;
    }
    
    public final String getDescription()
    {
        return m_strDescription;
    }
    
    public final byte[] getBytesImageS()
    {
        return m_bImageSmall;
    }
    
    public void setPositiveVotes(int nNewValue)
    {
        m_nPositiveVotes = nNewValue;
    }
    
    public void setNegativeVotes(int nNewValue)
    {
        m_nNegativeVotes = nNewValue;
    }
}