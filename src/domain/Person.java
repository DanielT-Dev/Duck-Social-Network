package domain;

public class Person extends User {
    private final String nume;
    private final String prenume;
    private final String dataNasterii;
    private final String ocupatie;
    private final long nivelEmpatie;


    public Person(long id, String username, String email, String password, String nume, String prenume, String dataNasterii, String ocupatie, long nivelEmpatie) {
        super(id, username, email, password);
        this.nume = nume;
        this.prenume = prenume;
        this.dataNasterii = dataNasterii;
        this.ocupatie = ocupatie;
        this.nivelEmpatie = nivelEmpatie;
    }

    public String getNume() {
        return nume;
    }
    public String getPrenume() {
        return prenume;
    }
    public String getDataNasterii() {
        return dataNasterii;
    }
    public String getOcupatie() {
        return ocupatie;
    }
    public long getNivelEmpatie() {
        return nivelEmpatie;
    }

    @Override
    public String toString() {
        return super.toString() +", nume: " + this.getNume() + " prenume: " + this.getPrenume() + " dataNasterii: " + this.getDataNasterii()
                + " ocupatie: " + this.getOcupatie() +  " nivelEmpatie: " + this.getNivelEmpatie();
    }
}
