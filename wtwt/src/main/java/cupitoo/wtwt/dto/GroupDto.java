package cupitoo.wtwt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cupitoo.wtwt.model.group.Group;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
@Slf4j
public class GroupDto {
    private Long id;
    private Integer dday;
    private String image;
    private String name;
    private LocalDate firstDay;
    private LocalDate lastDay;
    private UserProfile leader;
    private List<String> tags = new ArrayList<>();
    private List<UserProfile> members = new ArrayList<>();
    private List<StringDataDto> notices = new ArrayList<>();
    private List<HyperlinkDto> places = new ArrayList<>();
    private List<StringDataDto> memos = new ArrayList<>();

    public GroupDto(Group group) {
        this.id = group.getId();
        this.dday = getDday(group.getFirstDay());
        if(group.getGroupImage() != null) {
            this.image = group.getGroupImage().getUrl();
        }
        this.name = group.getGroupName();
        this.firstDay = group.getFirstDay();
        this.lastDay = group.getLastDay();
        this.leader = new UserProfile(group.getLeader());
        if(group.getTags() != null) {
            this.tags =  group.getTags().stream()
                    .map(t -> t.getTag().getContents())
                    .collect(Collectors.toList());
        }
        if(group.getMembers() != null) {
            this.members = group.getMembers().stream()
                    .map(m -> new UserProfile(m.getUser()))
                    .collect(Collectors.toList());
        }

        if(group.getNotices().size() > 0) {
            this.notices = group.getNotices().stream()
                    .map(i -> new StringDataDto(i))
                    .collect(Collectors.toList());
        }

        if(group.getLinks().size() > 0) {
            this.places = group.getLinks().stream()
                    .map(i -> new HyperlinkDto(i))
                    .collect(Collectors.toList());
        }

        if(group.getMemos().size() > 0) {
            this.memos = group.getMemos().stream()
                    .map(i -> new StringDataDto(i))
                    .collect(Collectors.toList());
        }
    }

    private int getDday(LocalDate toDate) {
        LocalDate fromDate = LocalDate.now();
        return (int) DAYS.between(fromDate, toDate);
    }
}