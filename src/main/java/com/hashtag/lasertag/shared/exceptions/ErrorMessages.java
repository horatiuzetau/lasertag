package com.hashtag.lasertag.shared.exceptions;

public class ErrorMessages {

  public static String BUNDLED_SLOTS_DONT_RESPECT_BUNDLE_TIME_RANGE = "Activitatile trebuie sa fie rezervate in interiorul programului de pachet!";
  public static String TIME_PROVIDED_NOT_IN_SCHEDULE = "Timpul care s-a mentionat nu respecta orarul!";
  public static String SHOULD_BOOK_ALL_SLOTS_FROM_BUNDLE = "Trebuie sa alegeti un interval de timp pentru fiecare activitate din pachet!";
  public static String INTERNAL_SERVER_ERROR = "Oops! A aparut o problema. Va rugam sa contactati supoprtul!";
  public static String SERVICE_UNAVAILABLE = "Momentan, serviciul de rezervari este indisponibil! Va rugam sa reveniti mai tarziu";
  public static String ACTIVITY_NOT_FOUND = "Se pare ca serviciul pe care l-ati ales nu poate fi gasit! Va rugam sa dati refresh si sa incercati din nou!";
  public static String SCHEDULE_NOT_FOUND = "Se pare ca orarul pe care l-ati ales nu poate fi gasit!";
  public static String ACTIVITY_NOT_ACTIVE = "Serviciul %s este inactiv! Activati-l si incercati din nou!";
  public static String CANT_DEACTIVATE_MAIN_SCHEDULE = "Nu se poate dezactiva un orar principal!";
  public static String CANT_DELETE_MAIN_SCHEDULE = "Nu se poate sterge un orar principal!";
  public static String STATUS_UPDATE_INVALID = "Statusul nu se poate actualiza!";
  public static String TOO_MANY_SPOTS_TO_BOOK = "Numarul de spatii a intrecut capacitatea! Este posibil ca altcineva sa fi rezervat inaintea dumneavoastra! Va rugam sa dati refresh si sa incercati din nou!";
  public static String BUNDLED_SLOTS_IN_REQUEST_NOT_FOUND = "Va rugam sa selectati orele pentru serviciile incluse in pachet!";
  public static String SCHEDULE_PERIOD_CONFLICT = "Perioada orarului este deja atribuita altui orar! Va rugam sa incercati alta perioada!";
  public static String START_DATE_BEFORE_END_DATE = "Data de inceput trebuie sa se afle dupa data de final a perioadei!";
  public static String TOO_FEW_TIME_FOR_BUNDLED_ACTIVITIES = "Timpul tuturor activitatilor incluse trebuie sa fie mai mic decat durata pachetului!";
  public static String BUNDLE_SHOULD_NOT_REMAIN_WITHOUT_BUNDLED_ITEMS = "Un pachet trebuie sa contina cel putin un serviciu!";
}
