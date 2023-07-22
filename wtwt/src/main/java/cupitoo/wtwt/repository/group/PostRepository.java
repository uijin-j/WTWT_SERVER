package cupitoo.wtwt.repository.group;

import cupitoo.wtwt.model.Group.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
