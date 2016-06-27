package com.boguenon.service.modules.bayes.common;

import java.util.HashMap;
import java.util.Map;

public class JTable {
	private int _columnCount;
	private int _rowCount;
	
	private String _name;
	
	private Map<String, Object> _data;
	private Map<Integer, String> _headers;
	
	public JTable() {
		this._headers = new HashMap<Integer, String>();
		this._data = new HashMap<String, Object>();
	}
	
	public int getColumnCount()
	{
		return _columnCount;
	}
	
	public void setColumnCount(int n)
	{
		this._columnCount = n;
	}
	
	public int getRowCount()
	{
		return _rowCount;
	}
	
	public void setRowCount(int n)
	{
		this._rowCount = n;
	}
	
	public void setNumRows(int n)
	{
		this._rowCount = n;
	}
	
	public void setHeaderValue(int n, String value)
	{
		this._headers.put(n, value);
	}
	
	public int getFirstEditableRow()
	{
		return 0;
	}

	public String getTableData()
	{
		String r = null;
		
		for(int column = 0; column < this.getColumnCount (); ++column)
        {
            for (int row = 0; row < this.getRowCount(); row++) {
            }
        }
		
		return r;
	}
	
	public void setName(String value)
	{
		this._name = value;
	}
	
	public Object getValueAt(int row, int col)
	{
		String k = "" + row + "," + col;
		
		return _data.get(k);
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		String k = "" + row + "," + col;
		this._data.put(k, value);
	}
}
