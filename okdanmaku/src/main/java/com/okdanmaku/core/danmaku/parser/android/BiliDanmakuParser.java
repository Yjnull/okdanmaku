package com.okdanmaku.core.danmaku.parser.android;

import com.okdanmaku.core.danmaku.model.DanmakuBase;
import com.okdanmaku.core.danmaku.model.android.Danmakus;
import com.okdanmaku.core.danmaku.parser.BiliDanmakuFactory;
import com.okdanmaku.core.danmaku.parser.DanmakuParserBase;
import com.okdanmaku.core.danmaku.parser.IDataSource;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;

/**
 * Created by yangya on 2019-11-19.
 */
public class BiliDanmakuParser extends DanmakuParserBase {

    static {
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
    }

    private final float dispWidth;
    public BiliDanmakuParser(int width) {
        this.dispWidth = width;
    }

    @Override
    public Danmakus parse(IDataSource dataSource) {
        if (dataSource != null) {
            AndroidFileSource source = (AndroidFileSource) dataSource;
            try {
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                XmlContentHandler contentHandler = new XmlContentHandler();
                xmlReader.setContentHandler(contentHandler);
                xmlReader.parse(new InputSource(source.mInputStream));
                return contentHandler.getResult();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                dataSource.release();
            }
        }

        return null;
    }

    private class XmlContentHandler extends DefaultHandler {
        private Danmakus result = null;
        private DanmakuBase item = null;
        private boolean completed = false;
        private int index = 0;

        public Danmakus getResult() {
            return result;
        }

        private String decodeXmlString(String title) {
            if (title.contains("&amp;")) {
                title = title.replace("&amp;", "&");
            }
            if (title.contains("&quot;")) {
                title = title.replace("&quot;", "\"");
            }
            if (title.contains("&gt;")) {
                title = title.replace("&gt;", ">");
            }
            if (title.contains("&lt;")) {
                title = title.replace("&lt;", "<");
            }
            return title;
        }

        @Override
        public void startDocument() throws SAXException {
            result = new Danmakus();
        }

        @Override
        public void endDocument() throws SAXException {
            completed = true;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String tagName = localName.length() != 0 ? localName : qName;
            tagName = tagName.toLowerCase().trim();
            if ("d".equals(tagName)) {
                String pValue = attributes.getValue("p");
                String[] values = pValue.split(",");
                if (values.length > 0) {
                    long time = (long) (Float.parseFloat(values[0]) * 1000); // 出现时间
                    int type = Integer.parseInt(values[1]); // 弹幕类型
                    float textSize = Float.parseFloat(values[2]); // 字体大小
                    int color = Integer.parseInt(values[3]) | 0xFF000000; // 颜色
                    // int poolType = Integer.parseInt(values[5]); // 弹幕池类型（忽略)
                    item = BiliDanmakuFactory.createDanmaku(type, dispWidth);
                    if (item != null) {
                        item.time = time;
                        item.textSize = textSize;
                        item.textColor = color;
                    }
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (item != null) {
                String tagName = localName.length() != 0 ? localName : qName;
                if ("d".equalsIgnoreCase(tagName)) {
                    result.addItem(item);
                }
                item = null;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (item != null) {
                item.text = decodeXmlString(new String(ch, start, length));
                item.index = index++;
            }
        }
    }

}
