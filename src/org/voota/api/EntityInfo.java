/*
 * This file is part of the Voota package.
 * (c) 2010 Tatyana Ulyanova <levkatata.voota@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

/**
 * This file contains implementation of EntityInfo class. This class includes all
 * information about one entity - politician or party.
 *
 * @package    Voota
 * @subpackage Api
 * @author     Tatyana Ulyanova
 * @version    1.0
 */

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
    
    /**
     * Constructor of class EntityInfo that creates object based on 
     * appropriate JSONObject data. Constructor parses json object and extracts
     * each field by key. Constructor also loads small image data from server.
     *
     * @param  jsonEntity    object in json format contained appropriate entity fields
     * @throws JSONException if JSONObject parameter doesn't contain some field
     * @see <a href="http://www.json.org/javadoc/org/json/JSONObject.html">JSONObject</a>
     * @see <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
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
    
    /**
     * Returns a unique identifier of the entity within a type.  
     *
     * @return entity ID
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public long getID()
    {
        return m_lID;
    }
    
    /**
     * Returns entity type.  
     *
     * @return value of {@link EntityType} emun
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     * @see    EntityInfo#EntityType EntityType
     */
    public EntityType getType()
    {
        return m_type;
    }
    
    /**
     * Returns entity name.  
     *
     * @return entity name
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public String getName()
    {
        return m_strName;
    }
    
    /**
     * Returns entity long name.  
     *
     * @return entity long name
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public String getLongName()
    {
        return m_strLongName;
    }
    
    /**
     * Returns String object represents color image url to download. It returns String
     * object because url could contain some special characters which would converted
     * in used charset in future calls.  
     *
     * @return image url in String object
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public String getUrlImage()
    {
        return m_strImage;
    }
    
    /**
     * Returns String object represents black-write image url to download. It returns String
     * object because url could contain some special characters which would converted
     * in used charset in future calls.  
     *
     * @return black-white image url in String object
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public String getUrlImageBW()
    {
        return m_strImageBW;
    }
    
    /**
     * Returns String object represents small color image url to download. 
     * It returns String object because url could contain some special characters 
     * which would converted in used charset in future calls.  
     *
     * @return small image url in String object
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public String getUrlImageSmall()
    {
        return m_strImageS;
    }
    
    /**
     * Returns String object represents small black-write image url to download. 
     * It returns String object because url could contain some special characters 
     * which would converted in used charset in future calls.  
     *
     * @return small black-white image url in String object
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public String getUrlImageBWSmall()
    {
        return m_strImageSBW;
    }
    
    /**
     * Returns positive votes.
     *
     * @return positive votes
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public int getPositiveVotes()
    {
        return m_nPositiveVotes;
    }
    
    /**
     * Returns recent positive votes.
     *
     * @return recent positive votes
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public int getRecPositiveVotes()
    {
        return m_nRecPositiveVotes;
    }
    
    /**
     * Returns negative votes.
     *
     * @return negative votes
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public int getNegativeVotes()
    {
        return m_nNegativeVotes;
    }
    
    /**
     * Returns recent negative votes.
     *
     * @return positive votes
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public int getRecNegativeVotes()
    {
        return m_nRecNegativeVotes;
    }
    
    /**
     * Returns entity description.
     *
     * @return entity description
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public String getDescription()
    {
        return m_strDescription;
    }
    
    /**
     * Returns byte array contains small image data was loaded.
     *
     * @return small image data in byte array
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public byte[] getBytesImageS()
    {
        return m_bImageSmall;
    }
    
    /**
     * Set new value of positive votes.
     *
     * @param nNewValue new value of positive votes
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public void setPositiveVotes(int nNewValue)
    {
        m_nPositiveVotes = nNewValue;
    }
    
    /**
     * Set new value of negative votes.
     *
     * @param nNewValue new value of negative votes
     * @see    <a href="http://trac.voota.org/wiki/entity_element">Entity element</a>
     */
    public void setNegativeVotes(int nNewValue)
    {
        m_nNegativeVotes = nNewValue;
    }
}