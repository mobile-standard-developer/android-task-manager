package android.jpsan.es;

public class Task {
	private String title;
	private String date;
	private int date_i;
	private String notes;
	
	public Task(String t, String d, String n) {
		this.title = t;
		this.date =d;
		this.notes = n;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getDate_i() {
		return date_i;
	}

	public void setDate_i(int date_i) {
		this.date_i = date_i;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
}
