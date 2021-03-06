/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is the Bugzilla Testopia Java API.
 *
 * The Initial Developer of the Original Code is Andrew Nelson.
 * Portions created by Andrew Nelson are Copyright (C) 2006
 * Novell. All Rights Reserved.
 *
 * Contributor(s): Andrew Nelson <anelson@novell.com>
 *
 */
package tcms.API;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

import com.redhat.qe.xmlrpc.Session;

/**
 * Allows the user to get a productID from it's name, or it's name from the productID. It's also able to return a
 * product's milestones'.
 * @author anelson
 *
 */
public class Product extends TestopiaObject{

	protected String productName;

	/**
	 * 
	 * @param userName - your testopia/bugzilla username
	 * @param password - the password for your account 
	 * @param login - the user you want attributes returned for
	 * @param url - the url of the testopia server
	 */
	public Product(Session session)
	{
		this.session = session;
		this.id = newIntegerAttribute("id", null);
	}
	
	public Product(Session session, String productName) throws XmlRpcException
	{
		this.session = session;
		this.id = newIntegerAttribute("id", null);
		this.productName = productName;
		this.getProductIDByName(productName);
	}

	/**
	 * 
	 * @param productName the name of the product, that the ID will be returned for. 0 Will be 
	 * returned if the product can't be found
	 * @return the ID of the specified product
	 * @throws XmlRpcException 
	 */
	public int getProductIDByName(String productName)
	throws XmlRpcException
	{
		get("Product.check_product", productName);	
		return getId();
	}

	public int getCategoryIDByName(String categoryName, String productName) throws XmlRpcException
	{
		Map m = (Map)callXmlrpcMethod("Product.check_category", categoryName, productName);
		return (Integer)m.get("id");		 

	}

	public int getComponentIDByName(String componentName, String productName) throws XmlRpcException
	{
		Map m = (Map)callXmlrpcMethod("Product.check_component", componentName, productName);
		return (Integer)m.get("id");		 

	}
	
	public int getVersionIDByName(String version)throws XmlRpcException {
		/*Map params = new HashMap();
		params.put("name", version);
		List list = (List)callXmlrpcMethod("Product.filter_versions", params);
		return (Integer)((Map) list.get(0)).get("id");*/
		
		Object[] list = (Object[])callXmlrpcMethod("Product.get_versions", productName);
		for (Object item: list){
			Map map = (Map)item;
			if (item != null & map.get("value").equals(version)) return (Integer)map.get("id");
		}
		throw new IllegalStateException("Version " + version + "was not found.");

	}

	public String getName(){
		return productName;
	}
	/**
	 * 
	 * @param id the ID of the product name that will be returned. Null is returned 
	 * if the product can't be found
	 * @return the product name that corresponds the specified product ID
	 * @throws XmlRpcException 
	 */
	public String getProductNameByID(int id)
	throws XmlRpcException
	{
		return (String)callXmlrpcMethod("Product.get", id);
	}

	public Object[] getMilestones(int id)
	throws XmlRpcException
	{
		return (Object[])callXmlrpcMethod("Product.get_milestones", id);
	}
}
