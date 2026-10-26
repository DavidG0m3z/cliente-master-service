package co.com.davidgomez.model.customer;

public record IdentityDocument(String type, String number) {

    public static IdentityDocument of(String rawType, String rawNumber){

        if(rawType == null || rawType.isBlank() || rawNumber == null || rawNumber.isBlank()) {
            throw new IllegalArgumentException("Datos incompletps");
        }

        String type = rawType.trim().toUpperCase();

        String number = rawNumber
                .replaceAll("[^0-9A-Za-z]","")
                .toUpperCase()
                .replaceFirst("^0+(?!$)","");

        if(number.isEmpty()){
            throw new IllegalArgumentException("Numero de documento invalido");
        }

        return new IdentityDocument(type,number);

    }

    public String key(){
        return type + "-" + number;
    }
}
