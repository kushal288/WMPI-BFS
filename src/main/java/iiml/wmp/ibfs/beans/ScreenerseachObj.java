package iiml.wmp.ibfs.beans;

public class ScreenerseachObj
{
	private String url;
	private Integer id;
	private String name;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "ScreenerseachObj{" + "url='" + url + '\'' + ", id=" + id + ", name='" + name + '\'' + '}';
	}
}


