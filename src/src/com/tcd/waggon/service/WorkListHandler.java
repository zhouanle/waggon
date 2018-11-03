package com.tcd.waggon.service;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.tcd.waggon.Constants;
import com.tcd.waggon.data.Line;
import com.tcd.waggon.data.Tower;
import com.tcd.waggon.data.WorkList;

import android.util.Log;

public class WorkListHandler extends DefaultHandler {
	private StringBuilder cache = new StringBuilder(1024);
	private boolean firstElement = true;
	
	private static final String NODE_ROOT = "WorkList";
	private static final String NODE_LINE = "Line";
	private static final String NODE_TOWER = "Tower";
	
	private static final String ATTR_ID = "id";
	private static final String ATTR_OPERATOR = "operator";
	private static final String ATTR_DATE = "execute_date";
	private static final String ATTR_DESC = "description";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_LAT = "lat";
	private static final String ATTR_LONG = "long";
	private static final String ATTR_ADDR = "addr";
	
	private static final Set<String> fetchChars = new HashSet<String>();

	private WorkListAdapter mListener;
	private Line l;
	private Tower t;
	
	static {
	    fetchChars.add(NODE_ROOT); 
	    fetchChars.add(NODE_LINE);
	    fetchChars.add(NODE_TOWER);
	    
	    fetchChars.add(ATTR_ID);
	    fetchChars.add(ATTR_OPERATOR);
	    fetchChars.add(ATTR_DATE);
	    fetchChars.add(ATTR_DESC);
	    fetchChars.add(ATTR_NAME);
	    fetchChars.add(ATTR_TYPE);
	    fetchChars.add(ATTR_LAT);
	    fetchChars.add(ATTR_LONG);
	    fetchChars.add(ATTR_ADDR);
	}
	    
	public WorkListHandler(WorkListAdapter listener) {
		mListener = listener;
	}
	
	@Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (cache!=null)
            cache.append(ch, start, length);
    }
	
	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
		//if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "end localName = " + localName);
		
		if (NODE_ROOT.equals(localName)) {
			if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "End of " + NODE_ROOT);
            return;
        }
		
		if (NODE_LINE.equals(localName)) {
			if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "End of " + NODE_LINE);
			l = null;
            return;
        }
		
		if (NODE_TOWER.equals(localName)) {
			if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "End of " + NODE_TOWER);
			t = null;
            return;
        }
		
        /*if ((mCurrentBook != null) && (cache != null)) {
            // Log.d(Constant.LOG_TAG, "mCurrentBook=" + mCurrentBook + ", cache=" + cache);
            if (NODE_BOOK_TITLE.equals(localName))
                mCurrentBook.setTitle(cache.toString());
            else if (NODE_BOOK_SIZE.equals(localName))
                mCurrentBook.setSize(cache.toString());
            else if (NODE_BOOK_INTRO.equals(localName))
                mCurrentBook.setIntro(cache.toString());
            else if (NODE_BOOK_THUMB.equals(localName))
                mCurrentBook.setThumb(cache.toString());
            else if (NODE_BOOK_AUTHOR.equals(localName))
                mCurrentBook.setAuthor(cache.toString());
            else if (NODE_BOOK_CATE.equals(localName))
                mCurrentBook.setCategory(cache.toString());
            else if (NODE_BOOK_WORD_COUNT.equals(localName))
                mCurrentBook.setWordCount(cache.toString());
        }*/
	}
	
	@Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "start localName = " + localName);
        if (firstElement) {
        	if (NODE_ROOT.equals(localName)) {
        		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Start of " + NODE_ROOT);
        		
        		WorkList list = mListener.getWorkList();
        		
        		String val = attributes.getValue(ATTR_ID);
        		if (val == null || "".equals(val.trim())) {
        		    // TODO should handle error here
        		} else {
        			list.setId(val);
        		}
        		val = attributes.getValue(ATTR_OPERATOR);
        		if (val == null || "".equals(val.trim())) {
        			// TODO should handle error here
        		} else {
        		    list.setOperator(val);
        		}
        		val = attributes.getValue(ATTR_DATE);
        		if (val == null || "".equals(val.trim())) {
        			// TODO should handle error here
        		}else {
        			list.setExecute_date(val);
        		}
        		val = attributes.getValue(ATTR_DESC);
        		if (val == null || "".equals(val.trim())) {
        			// TODO should handle error here
        		}else {
        			list.setDesc(val);
        		}
        	} else {
        		Log.e(Constants.LOG_TAG, "Unknown type '<" + localName + ">'.");
                throw new SAXException("Unknown type '<" + localName + ">'.");
            }
            firstElement = false;
            return;
        }
        
        if (NODE_LINE.equals(localName)) {
        	if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Start of " + NODE_LINE);
        	
        	// add new line
        	l = new Line();
        	l.setId(attributes.getValue(ATTR_ID));
        	l.setName(attributes.getValue(ATTR_NAME));
        	mListener.onLineAdded(l);
        	
            return;
        }
        
        if (NODE_TOWER.equals(localName)) {
        	if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Start of " + NODE_TOWER);
        	
        	// add new tower
        	t = new Tower();
        	t.setId(attributes.getValue(ATTR_ID));
        	t.setType(Integer.parseInt(attributes.getValue(ATTR_TYPE)));
        	t.setLat(Double.parseDouble(attributes.getValue(ATTR_LAT)));
        	t.setLon(Double.parseDouble(attributes.getValue(ATTR_LONG)));
        	t.setAddr(attributes.getValue(ATTR_ADDR));
			mListener.onTowerAdded(l, t);
			
            return;
        }
        
        if (fetchChars.contains(localName))
            cache = new StringBuilder(1024);
        else
            cache = null;
	}
}
