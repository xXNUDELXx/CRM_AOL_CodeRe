package pl.coderslab.service;

import java.util.List;

import pl.coderslab.entity.User;

public interface UserService {
	
	/**Returns User with given email
	 * 
	 * @param email
	 * @return User
	 */
	User findByEmail(String email);
	
	/**Saves gicen User to database
	 * 
	 * @param user
	 */
	void saveUser(User user);
	
	/**Returns list of all Users
	 * 
	 * @return list of Users
	 */
	List<User> findAll();
	
	/**Returns list of Users with given user as their supervisor
	 * 
	 * @param user
	 * @return list of Users
	 */
	List<User> findBySupervisor(User user);
	
	/**Returns max value of contract that user can accept from all his roles
	 * 
	 * @param user
	 * @return max value of contract that user can accept
	 */
	Double getMaxContractValue(User user);

	public UserDetails loadUserDetailsByEmail(String email) {
    User user = findByEmail(email);
    if (user == null) {
        throw new UsernameNotFoundException(email);
    }

    Set<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toSet());

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), grantedAuthorities);
}


}
