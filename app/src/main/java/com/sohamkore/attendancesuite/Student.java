package com.sohamkore.attendancesuite;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    private String studentId;
    private String name;
    private String rollNo;
    private String department;
    private String studentClass;
    private String email;
    private String contact;
    private String dob;
    private String address;
    private String password;

    // Default constructor (required for Firebase)
    public Student() {}

    // Parameterized constructor with studentId
    public Student(String studentId, String name, String rollNo, String department, String studentClass,
                   String email, String password, String contact, String dob, String address) {
        this.studentId = studentId;
        this.name = name;
        this.rollNo = rollNo;
        this.department = department;
        this.studentClass = studentClass;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.dob = dob;
        this.address = address;
    }

    // Getter and setter methods
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(studentId);
        parcel.writeString(name);
        parcel.writeString(rollNo);
        parcel.writeString(department);
        parcel.writeString(studentClass);
        parcel.writeString(email);
        parcel.writeString(contact);
        parcel.writeString(dob);
        parcel.writeString(address);
        parcel.writeString(password);
    }

    protected Student(Parcel in) {
        studentId = in.readString();
        name = in.readString();
        rollNo = in.readString();
        department = in.readString();
        studentClass = in.readString();
        email = in.readString();
        contact = in.readString();
        dob = in.readString();
        address = in.readString();
        password = in.readString();
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}
