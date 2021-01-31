package com.agnux.monkeybusiness;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

public class BucketFile implements StrangeStreamSource {

    Runtime runtime;
    String cmd;

    BucketFile(final String bucket, final String file) {

        this.runtime = Runtime.getRuntime();
        this.cmd = BucketFile.gearUpCmd(bucket, file);
    }

    private static InputStream TurnPipeIntoStream(final String cmd, Runtime rt) throws IOException {

        Process p = rt.exec(cmd);
        return p.getInputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {

        return BucketFile.TurnPipeIntoStream(this.cmd, this.runtime);
    }

    @Override
    public int calcSize() {
        return 0;
    }

    @Override
    public String getMimeType() {

        return "hola mundo";
    }

    private static String gearUpCmd(final String s3Bucket, final String s3file) {
        Map<String, String> valuesMap = new HashMap<String, String>();
        String templateString = "aws s3 cp s3://${bucket}/${file} -";
        {
            valuesMap.put("bucket", s3Bucket);
            valuesMap.put("file", s3file);
        }
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        return sub.replace(templateString);
    }
}
