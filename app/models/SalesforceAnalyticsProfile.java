/**
 * This document is a part of the source code and related artifacts
 * for GA2SA, an open source code for Google Analytics to 
 * Salesforce Analytics integration.
 *
 * Copyright © 2015 Cervello Inc.,
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the salesforce_analytics_profiles database table.
 * @author Igor Ivarov
 * @editor Sergey Legostaev 
 */
@Entity
@Table(name="salesforce_analytics_profiles")
public class SalesforceAnalyticsProfile extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@NotNull
	@NotEmpty
	@Column(name="application_name")
	@Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Not allow special characters, including spaces")
	private String applicationName;
	
	@NotNull
	@NotEmpty
	private String password;
	
	@NotNull
	@NotEmpty
	private String username;
	
	@NotNull
	@NotEmpty
	private String name;
	
	@Column(name = "salesforce_account_type")
	@Enumerated(EnumType.STRING)
	public SFAccountType accountType;

	//bi-directional many-to-one association to Job
	@JsonIgnore
	@OneToMany(mappedBy="salesforceAnalyticsProfile", fetch = FetchType.LAZY)
	public List<DatasetJob> datasetJobs;
	
	//bi-directional many-to-one association to Job
	@JsonIgnore
	@OneToMany(mappedBy="salesforceAnalyticsProfile", fetch = FetchType.LAZY)
	public List<DashboardJob> dashboardJobs;

	public SalesforceAnalyticsProfile() {
	}


	public String getApplicationName() {
		return this.applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}