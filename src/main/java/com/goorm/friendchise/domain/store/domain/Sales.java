package com.goorm.friendchise.domain.store.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "sales")
public class Sales {

    @Id
    private String id;

    private String d;
}
