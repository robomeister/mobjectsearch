package cp4ba.mobjectsearch;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MobjectSearchTest 
{
	public static void main(String[] args) 
	{
		//For testing only, don't worry about cert or hostname verification.
		//Helpful when running request through a reverse proxy for tracing
		TrustManager[] trustAllCerts = new TrustManager[] 
		{ 
		  new X509TrustManager() 
		  {
		    public java.security.cert.X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
		  } 
		};

		HostnameVerifier verifier = new HostnameVerifier() 
		{
			@Override
			public boolean verify(String hostname, SSLSession session) {return true;}
		};    

       
		try
		{
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(verifier);

			MobjectSearch mobjectSearch = new MobjectSearch();
			
			String ceURI = "https://<cpe-url>/wsi/FNCEWS40MTOM/";
			String objectStore = "<target object store name>";
			String objectClass = "<full mobject class name (not display name)>";
			String userName = "<ldap user that can connect to FileNet>";
			String password = "<password>";

			//search key/value pairs
			//the key represents a key in the mobject json file
			//the value represents the value of the key
			//e.g.
			// {
			//    "name": "bob",
			//    "address": "123 Main Street"
			// }
			// "name" is the key, "bob" is the value
			// "address" is the key, "123 Main Street" is the value
			// the findMobjects call wraps the value with *'s to perform a wildcard search (*value*)
			Map<String,String> map = new HashMap<String,String>();
			map.put("objectStore", "BAW");
			//map.put("customerNumber", "6JF7VQ8");
			
			Map<String,String> returnMap = mobjectSearch.findMobjects(map, ceURI, objectStore, objectClass, userName, password);
			
			if (returnMap != null)
			{
			   Collection<String> c = returnMap.values();
			   
			   Object[] mobjects =c.toArray();
  			   for (Object mobject: mobjects)
  			   System.out.println(mobject);
			}
			else
				System.out.println("No mobjects");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
