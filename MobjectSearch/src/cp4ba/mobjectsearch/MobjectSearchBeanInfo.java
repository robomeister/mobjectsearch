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

import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.Map;

public class MobjectSearchBeanInfo extends SimpleBeanInfo 
{
	  @SuppressWarnings("rawtypes")
	  private final Class beanClass = MobjectSearch.class;
	  
	  @Override
	  public MethodDescriptor[] getMethodDescriptors() 
	  {
	    try 
	    {
	      MethodDescriptor descriptorList[] = {findMobjects()};
	      return descriptorList;
	    } 
	    catch (Exception e) 
	    {
	      e.printStackTrace();
	      return super.getMethodDescriptors();
	    }
	  }
	  
	  @SuppressWarnings("unchecked")
	  private MethodDescriptor findMobjects() throws NoSuchMethodException 
	  {
	      Method method = beanClass.getMethod("findMobjects", Map.class, String.class, String.class, String.class, String.class, String.class);

	      ParameterDescriptor param1 = new ParameterDescriptor();
	      param1.setShortDescription("A Map of key-value search pairs");
	      param1.setDisplayName("Search Key-Values");

	      ParameterDescriptor param2 = new ParameterDescriptor();
	      param2.setShortDescription("The FNCEWS40MTOM URL of the Content Engine.");
	      param2.setDisplayName("CPE URL");

	      ParameterDescriptor param3 = new ParameterDescriptor();
	      param3.setShortDescription("The Mobject Object Store (i.e. BAWTOS, etc.).");
	      param3.setDisplayName("Object Store");	      

	      ParameterDescriptor param4 = new ParameterDescriptor();
	      param4.setShortDescription("The Mobject Document Class.");
	      param4.setDisplayName("Mobject Class");
	      
	      ParameterDescriptor param5 = new ParameterDescriptor();
	      param5.setShortDescription("CPE User ID.");
	      param5.setDisplayName("User ID");	      
	      
	      ParameterDescriptor param6 = new ParameterDescriptor();
	      param6.setShortDescription("CPE Password.");
	      param6.setDisplayName("Password");
	      
	      MethodDescriptor methodDescriptor = new MethodDescriptor(method, new ParameterDescriptor[] { param1, param2, param3, param4, param5, param6 });

	      return methodDescriptor;
	  }
}
