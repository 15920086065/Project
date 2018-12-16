package com.caration.encryption;

public class CREncryption {
	static {
		System.loadLibrary("crencryption");
	}
	public static native boolean isEncryption();
}
