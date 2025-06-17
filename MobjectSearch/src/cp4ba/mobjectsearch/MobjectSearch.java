/*
USE OF THIS SOFTWARE IS AT YOUR SOLE RISK. 
ALL MATERIALS, INFORMATION, PRODUCTS, SOFTWARE, PROGRAMS, AND SERVICES ARE PROVIDED "AS IS," WITH NO WARRANTIES OR GUARANTEES WHATSOEVER. 
IBM EXPRESSLY DISCLAIMS TO THE FULLEST EXTENT PERMITTED BY LAW ALL EXPRESS, IMPLIED, STATUTORY, AND OTHER WARRANTIES, GUARANTEES, OR REPRESENTATIONS, INCLUDING, 
WITHOUT LIMITATION, THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT OF PROPRIETARY AND INTELLECTUAL PROPERTY RIGHTS. 
WITHOUT LIMITATION, IBM MAKES NO WARRANTY OR GUARANTEE THAT THIS WEB SITE WILL BE UNINTERRUPTED, TIMELY, SECURE, OR ERROR-FREE.

YOU UNDERSTAND AND AGREE THAT IF YOU DOWNLOAD OR OTHERWISE OBTAIN MATERIALS, INFORMATION, PRODUCTS, SOFTWARE, PROGRAMS, OR SERVICES FROM THIS WEB SITE, 
YOU DO SO AT YOUR OWN DISCRETION AND RISK AND THAT YOU WILL BE SOLELY RESPONSIBLE FOR ANY DAMAGES THAT MAY RESULT, INCLUDING LOSS OF DATA OR DAMAGE TO YOUR COMPUTER SYSTEM. 
SOME JURISDICTIONS DO NOT ALLOW THE EXCLUSION OF WARRANTIES, SO THE ABOVE EXCLUSIONS MAY NOT APPLY TO YOU.

LIMITATION OF LIABILITY

TO THE FULLEST EXTENT PERMITTED BY APPLICABLE LAW, IN NO EVENT WILL IBM BE LIABLE TO ANY PARTY FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY OR CONSEQUENTIAL DAMAGES OF ANY TYPE WHATSOEVER RELATED TO OR ARISING FROM THIS WEB SITE OR ANY USE OF THIS WEB SITE, 
OR OF ANY SITE OR RESOURCE LINKED TO, REFERENCED, OR ACCESSED THROUGH THIS WEB SITE, OR FOR THE USE OR DOWNLOADING OF, OR ACCESS TO, ANY MATERIALS, INFORMATION, PRODUCTS, 
OR SERVICES, INCLUDING, WITHOUT LIMITATION, ANY LOST PROFITS, BUSINESS INTERRUPTION, LOST SAVINGS OR LOSS OF PROGRAMS OR OTHER DATA, 
EVEN IF IBM IS EXPRESSLY ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 

THIS EXCLUSION AND WAIVER OF LIABILITY APPLIES TO ALL CAUSES OF ACTION, WHETHER BASED ON CONTRACT, WARRANTY, TORT, OR ANY OTHER LEGAL THEORIES.
*/
package cp4ba.mobjectsearch;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import javax.security.auth.Subject;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;

import java.util.logging.Level;
import java.util.logging.Logger;



public class MobjectSearch
{
	private static final String CLASS_NAME = MobjectSearch.class.getName();
	protected static Logger logger = Logger.getLogger(CLASS_NAME);
	
	
	private Connection getCEConnection(String ceURI, String userName, String password) throws Exception 
	{
		String methodName="getCEConnection";
		logger.entering(CLASS_NAME, methodName);

		logger.logp(Level.FINER,CLASS_NAME,methodName,"Getting CE Connection to [" + ceURI + "] with user [" + userName + "]");
		Connection conn = null;
		try 
		{
			conn = Factory.Connection.getConnection(ceURI);
			Subject subject = UserContext.createSubject(conn, userName,password, "FileNetP8WSI");
			UserContext uc = UserContext.get();
			uc.pushSubject(subject);
		} 
		catch (Exception e) 
		{
			logger.logp(Level.SEVERE,CLASS_NAME,methodName,"CE Connection failed: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}

		logger.exiting(CLASS_NAME, methodName);
		return conn;
	}

	@SuppressWarnings("rawtypes")
	private String[] fetchObjects(String keyWords, String url, String objectStore, String documentClass, String userID, String password) 
	{
		String methodName="fetchObjects";
		logger.entering(CLASS_NAME, methodName);
		
		String[] mobjects=null;
		ArrayList<String> mobjectArray = new ArrayList<String>();
		
		try 
		{
			Connection conn = getCEConnection(url, userID, password);
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);
			ObjectStore objStore = Factory.ObjectStore.fetchInstance(domain,objectStore, null);

			SearchScope search = new SearchScope(objStore);
			String searchString= "SELECT d.this, DocumentTitle,ClassDescription,Name,ContentSize,ContentElementsPresent,ContentElements FROM Document d INNER JOIN ContentSearch v ON v.QueriedObject= d.This WHERE IsClass(d,"+documentClass+") AND d.IsCurrentVersion=TRUE AND CONTAINS(d.*,'" + keyWords+ "')";
			logger.logp(Level.FINER,CLASS_NAME,methodName,"Mobject search string [" + searchString + "]");
			SearchSQL sql= new SearchSQL(searchString); 
			DocumentSet documents = (DocumentSet) search.fetchObjects(sql,Integer.valueOf("0"),null, Boolean.valueOf(true));
			com.filenet.api.core.Document doc; 

			Iterator it = documents.iterator();
			while (it.hasNext())
			{
				doc = (Document)it.next();
				ContentElementList cel = doc.get_ContentElements();
				Iterator cel1 = cel.iterator();
				if (cel1.hasNext())
				{
					ContentTransfer ct = (ContentTransfer) cel1.next();
					InputStream stream = ct.accessContentStream();
					String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
					mobjectArray.add(text);
					stream.close();
				}

			}

		} 
		catch (Exception e) 
		{
			logger.logp(Level.SEVERE,CLASS_NAME,methodName,"Method fetchMobjects failed: " + e.getMessage());
			e.printStackTrace();
		}
		
		if (mobjectArray.size()>0)
		{
		   mobjects = new String[mobjectArray.size()];
		
		   for (int x=0;x<mobjects.length;x++)
	          mobjects[x] = mobjectArray.get(x);
		}
		
		logger.exiting(CLASS_NAME, methodName);
		return mobjects;

	}
	
	public Map<String, String> findMobjects(Map<String,String> searchKeyValues, String url, String objectStore, String documentClass, String userID, String password)
	{
		String methodName="findMobjects";
		logger.entering(CLASS_NAME, methodName);

		logger.logp(Level.FINE,CLASS_NAME, methodName, "Finding [" + documentClass + "] mobjects at [" + url + "] on object store [" + objectStore + "] with user [" + userID +"]");
		
		Map<String,String> returnMap = new HashMap<String,String>();
		StringBuffer searchString = new StringBuffer();
		
		Set<String> keys = searchKeyValues.keySet();
		Collection<String> values = searchKeyValues.values();
		
		Object[] keysArray = keys.toArray();
		Object[] valuesArray = values.toArray();
	
		for (int x=0; x<keysArray.length;x++)
		{
			//wild cards (*'s) are placed around the value
			String s = "\"" + keysArray[x] + "\"\\: \"*" + valuesArray[x] + "*\"";
			searchString.append(s);
			
			if (x < keysArray.length-1)
			   searchString.append(" AND ");
		}		
		
		String[] resultsArray = fetchObjects(searchString.toString(), url, objectStore, documentClass, userID, password);
		
		if (resultsArray==null)
		{
			logger.logp(Level.FINE,CLASS_NAME, methodName, "Exiting findMobjects returning null mobjects.");
			logger.exiting(CLASS_NAME, methodName);
			return null;
		}
		
		for (int x=0;x<resultsArray.length;x++)
			returnMap.put(String.valueOf(x), resultsArray[x]);
		
		logger.logp(Level.FINE,CLASS_NAME, methodName, "Exiting findMobjects returning ["+resultsArray.length+"] mobjects.");
		logger.exiting(CLASS_NAME, methodName);
		return returnMap;
	}
}