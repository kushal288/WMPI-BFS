package iiml.wmp.ibfs.beans;

public class ExcelBean
{

	private String Company;
	private String Rights_Ratio;
	private Integer FV;
	private Integer Premium;
	private String Announced;
	private String Record;
	private String Ex_Rights;
	private String Hyperlink;

	@Override
	public String toString()
	{
		return "ExcelBean{" + "Company='" + Company + '\'' + ", Rights_Ratio='" + Rights_Ratio + '\'' + ", FV=" + FV + ", Premium=" + Premium + ", Announced='"
				+ Announced + '\'' + ", Record='" + Record + '\'' + ", Ex_Rights='" + Ex_Rights + '\'' + ", Hyperlink='" + Hyperlink + '\'' + '}';
	}

	public String getCompany()
	{
		return Company;
	}

	public void setCompany(String company)
	{
		Company = company;
	}

	public String getRights_Ratio()
	{
		return Rights_Ratio;
	}

	public void setRights_Ratio(String rights_Ratio)
	{
		Rights_Ratio = rights_Ratio;
	}

	public Integer getFV()
	{
		return FV;
	}

	public void setFV(Integer FV)
	{
		this.FV = FV;
	}

	public Integer getPremium()
	{
		return Premium;
	}

	public void setPremium(Integer premium)
	{
		Premium = premium;
	}

	public String getAnnounced()
	{
		return Announced;
	}

	public void setAnnounced(String announced)
	{
		Announced = announced;
	}

	public String getRecord()
	{
		return Record;
	}

	public void setRecord(String record)
	{
		Record = record;
	}

	public String getEx_Rights()
	{
		return Ex_Rights;
	}

	public void setEx_Rights(String ex_Rights)
	{
		Ex_Rights = ex_Rights;
	}

	public String getHyperlink()
	{
		return Hyperlink;
	}

	public void setHyperlink(String hyperlink)
	{
		Hyperlink = hyperlink;
	}
}
