package main.api;

import lombok.Data;

@Data
public class ArchivedCard {

    private Long id;
    private String userId;
    private String text;
    private String date;
    private Long boardId;


}
