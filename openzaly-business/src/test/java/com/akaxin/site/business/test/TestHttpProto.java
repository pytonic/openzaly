package com.akaxin.site.business.test;

import java.io.IOException;

import com.akaxin.common.http.ZalyHttpClient;
import com.akaxin.proto.core.PluginProto;

public class TestHttpProto {
	public static void main(String args[]) throws Exception {
		testHttpPost();
		// testHttpGet();
	}

	private static byte[] testHttpPost() throws IOException {
		String pluginUrl = "http://192.168.1.106/siteMember/applyAddFriend";
		String siteUserId = "test1111";
		String requestParams = "Test";
		PluginProto.ProxyPackage proxyPackage = PluginProto.ProxyPackage.newBuilder()
				.putProxyContent(PluginProto.ProxyKey.CLIENT_SITE_USER_ID_VALUE, siteUserId).setData(requestParams)
				.build();
		byte[] httpResposne = ZalyHttpClient.getInstance().postBytes(pluginUrl, proxyPackage.toByteArray());
		
		System.out.println("response = " + httpResposne.length);

		return httpResposne;
	}

	private static byte[] testHttpGet() throws Exception {
		String siteUserId = "SAM123455";
		String url2 = "http://192.168.1.106/siteMember/members" + "?siteUserId=" + siteUserId;
		System.out.println("url = " + url2);
		byte[] httpResponse = ZalyHttpClient.getInstance().get(url2);

		return httpResponse;
	}
}
