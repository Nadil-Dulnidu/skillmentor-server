package com.skillmentor.root.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "classroom")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"sessionEntityList", "mentor"})
public class ClassRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_room_id")
    private Integer classRoomId;

    @NotNull(message = "Title must not be null")
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull(message = "Enrolled student count must not be null")
    @Column(name = "enrolled_student_count",columnDefinition = "INT DEFAULT 0")
    private Integer enrolledStudentCount;

    @NotBlank(message = "Class image must not be null")
    @Column(name = "class_image", nullable = false)
    @Schema(description = "Image URL for the classroom", example = "https://example.com/classroom.jpg")
    private String classImage;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentor_id", referencedColumnName = "mentor_id")
    private MentorEntity mentor;

    @OneToMany(mappedBy = "classRoomEntity", fetch = FetchType.EAGER)
    private List<SessionEntity> sessionEntityList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (enrolledStudentCount == null) {
            enrolledStudentCount = 0;
        }
    }
}
