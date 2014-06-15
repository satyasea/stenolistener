package com.nuance.nmdp.sample;

public class AppInfo
{
    /**
     * The login parameters should be specified in the following manner:
     * 
     * public static final String SpeechKitServer = "ndev.server.name";
     * 
     * public static final int SpeechKitPort = 1000;
     * 
     * public static final String SpeechKitAppId = "ExampleSpeechKitSampleID";
     * 
     * public static final byte[] SpeechKitApplicationKey =
     * {
     *     (byte)0x38, (byte)0x32, (byte)0x0e, (byte)0x46, (byte)0x4e, (byte)0x46, (byte)0x12, (byte)0x5c, (byte)0x50, (byte)0x1d,
     *     (byte)0x4a, (byte)0x39, (byte)0x4f, (byte)0x12, (byte)0x48, (byte)0x53, (byte)0x3e, (byte)0x5b, (byte)0x31, (byte)0x22,
     *     (byte)0x5d, (byte)0x4b, (byte)0x22, (byte)0x09, (byte)0x13, (byte)0x46, (byte)0x61, (byte)0x19, (byte)0x1f, (byte)0x2d,
     *     (byte)0x13, (byte)0x47, (byte)0x3d, (byte)0x58, (byte)0x30, (byte)0x29, (byte)0x56, (byte)0x04, (byte)0x20, (byte)0x33,
     *     (byte)0x27, (byte)0x0f, (byte)0x57, (byte)0x45, (byte)0x61, (byte)0x5f, (byte)0x25, (byte)0x0d, (byte)0x48, (byte)0x21,
     *     (byte)0x2a, (byte)0x62, (byte)0x46, (byte)0x64, (byte)0x54, (byte)0x4a, (byte)0x10, (byte)0x36, (byte)0x4f, (byte)0x64
     * };
     * 
     * Please note that all the specified values are non-functional
     * and are provided solely as an illustrative example.
     * 
     */

    /* Please contact Nuance to receive the necessary connection and login parameters */
    public static final String SpeechKitServer = "sandbox.nmdp.nuancemobility.net";

    public static final int SpeechKitPort = 443;
    
    public static final boolean SpeechKitSsl = false;

    public static final String SpeechKitAppId = "NMDPTRIAL_justsayit20140423022544";

    public static final byte[] SpeechKitApplicationKey = {
            (byte)0x39, (byte)0xc9, (byte)0xc0, (byte)0x5c, (byte)0x11, (byte)0x44, (byte)0xd2, (byte)0x90,
            (byte)0x7a, (byte)0xa5, (byte)0x31, (byte)0x3b, (byte)0xdc, (byte)0x26, (byte)0x89, (byte)0xa9,
            (byte)0x5c, (byte)0x4b, (byte)0x47, (byte)0x4c, (byte)0x24, (byte)0x17, (byte)0xee, (byte)0x7a,
            (byte)0x33, (byte)0xda, (byte)0x1b, (byte)0x20, (byte)0x61, (byte)0x28, (byte)0xda, (byte)0x9a,
            (byte)0xf1, (byte)0x9b, (byte)0xf7, (byte)0x03, (byte)0xdf, (byte)0xc9, (byte)0x93, (byte)0x59,
            (byte)0x6e, (byte)0xc4, (byte)0x94, (byte)0x4b, (byte)0xeb, (byte)0x1d, (byte)0xf4, (byte)0x47,
            (byte)0x68, (byte)0x3b, (byte)0x94, (byte)0x8d, (byte)0x53, (byte)0x82, (byte)0x07, (byte)0x86,
            (byte)0x5c, (byte)0x22, (byte)0xa6, (byte)0xbd, (byte)0x4b, (byte)0x81, (byte)0x58, (byte)0x3c
    };
}