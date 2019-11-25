/*********************************************************************
 * Copyright (c) 2018, Institute of Cancer Research
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *
 * (3) Neither the name of the Institute of Cancer Research nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************/
package org.nrg.xnatx.ohifviewer;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.security.UserI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jamesd
 */
public class Security
{
	private final static Logger logger = LoggerFactory.getLogger(Security.class);

	public static final String Create = "Create";
	public static final String Delete = "Delete";
	public static final String Edit = "Edit";
	public static final String Read = "Read";

	/**
	 *
	 * @param user
	 * @param element
	 * @param value
	 * @param permissions
	 * @throws PluginException
	 */
	public static void checkPermissions(UserI user, String element, String value,
		String... permissions) throws PluginException
	{
		if (user == null)
		{
			throw new PluginException("User must not be null",
				PluginCode.HttpUnprocessableEntity);
		}
		for (String permission : permissions)
		{
			checkPermission(user, element, value, permission);
		}
	}

	/**
	 *
	 * @param user
	 * @param itemData
	 * @param permissions
	 * @throws PluginException
	 */
	public static void checkPermissions(UserI user,
		XnatExperimentdata itemData, String... permissions)
		throws PluginException
	{
		if (user == null)
		{
			throw new PluginException("User must not be null",
				PluginCode.HttpUnprocessableEntity);
		}
		if (itemData == null)
		{
			throw new PluginException("Item must not be null",
				PluginCode.HttpUnprocessableEntity);
		}
		for (String permission : permissions)
		{
			checkPermission(user, itemData, permission);
		}
	}

	/**
	 * Checks whether the project exists and is visible to the user.
	 * @param user the user
	 * @param projectId the project ID
	 * @throws PluginException if the project doesn't exist or isn't visible to the user
	 */
	public static void checkProject(UserI user, String projectId)
		throws PluginException
	{
		if (user == null)
		{
			throw new PluginException("User must not be null",
				PluginCode.HttpUnprocessableEntity);
		}
		checkPermission(user, "xnat:projectData/ID", projectId, Read);
		XnatProjectdata data = XnatProjectdata.getXnatProjectdatasById(projectId,
			user, false);
		if (data == null)
		{
			throw new PluginException(
				"Bad project ID or not visible to user "+user.getUsername()+
					": "+projectId,
				PluginCode.HttpForbidden);
		}
		logger.debug("Project visible to user "+user.getUsername()+": "+projectId);
	}

	/**
	 * Checks whether the session exists and is visible to the user.
	 * @param user the user
	 * @param sessionId the session ID
	 * @throws PluginException if the session doesn't exist or isn't visible to the user
	 */
	public static void checkSession(UserI user, String sessionId)
		throws PluginException
	{
		if (user == null)
		{
			throw new PluginException("User must not be null",
				PluginCode.HttpUnprocessableEntity);
		}
		checkPermission(user, "xnat:imageSessionData/ID", sessionId, Read);
		XnatImagesessiondata data =
			XnatImagesessiondata.getXnatImagesessiondatasById(
				sessionId, user, false);
		if (data == null)
		{
			throw new PluginException(
				"Bad session ID or not visible to user "+user.getUsername()+": "+
					sessionId,
				PluginCode.HttpUnprocessableEntity);
		}
		logger.debug("Session visible to user "+user.getUsername()+": "+sessionId);
	}

	private static void checkPermission(UserI user,
		XnatExperimentdata itemData, String permission) throws PluginException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Permission check - User: "+user.getUsername()+
				", Permission: "+permission+", Item: "+itemData.getId());
		}
		try
		{
			switch (permission)
			{
				case Create:
					if (!Permissions.canCreate(user, itemData))
					{
						String message = "User "+user.getUsername()+
							" does not have create permission for item "+
							itemData.getId();
						throw new PluginException(message, PluginCode.HttpForbidden);
					}
					break;
				case Read:
					if (!Permissions.canRead(user, itemData))
					{
						String message = "User "+user.getUsername()+
							" does not have read permission for item "+
							itemData.getId();
						throw new PluginException(message, PluginCode.HttpForbidden);
					}
					break;
				case Edit:
					if (!Permissions.canEdit(user, itemData))
					{
						String message = "User "+user.getUsername()+
							" does not have edit permission for item "+
							itemData.getId();
						throw new PluginException(message, PluginCode.HttpForbidden);
					}
					break;
				case Delete:
					if (!Permissions.canDelete(user, itemData))
					{
						String message = "User "+user.getUsername()+
							" does not have delete permission for item "+
							itemData.getId();
						throw new PluginException(message, PluginCode.HttpForbidden);
					}
					break;
				default:
					throw new PluginException("Unsupported permission: "+permission,
						PluginCode.HttpForbidden);
			}
		}
		catch (PluginException ex)
		{
			throw ex;
		} // Yuk!
		catch (Exception ex)
		{
			throw new PluginException(
				"Item permission check error. User= "+user.getUsername()+
					" Permission= "+permission,
				PluginCode.HttpInternalError, ex);
		}
	}

	private static void checkPermission(UserI user, String element, String value,
		String permission) throws PluginException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Permission check - User: "+user.getUsername()+
				", Permission: "+permission+", Element: "+element+", Value: "+value);
		}
		try
		{
			switch (permission)
			{
				case Create:
					if (!Permissions.canCreate(user, element, value))
					{
						String message = "User "+user.getUsername()+
							" does not have create permission for "+element;
						throw new PluginException(message, PluginCode.HttpForbidden);
					}
					break;
				case Read:
					if (!Permissions.canRead(user, element, value))
					{
						String message = "User "+user.getUsername()+
							" does not have read permission for "+element;
						throw new PluginException(message, PluginCode.HttpForbidden);
					}
					break;
				case Edit:
					if (!Permissions.canEdit(user, element, value))
					{
						String message = "User "+user.getUsername()+
							" does not have edit permission for "+element;
						throw new PluginException(message, PluginCode.HttpForbidden);
					}
					break;
				case Delete:
					if (!Permissions.canDelete(user, element, value))
					{
						String message = "User "+user.getUsername()+
							" does not have delete permission for "+element;
						throw new PluginException(message, PluginCode.HttpForbidden);
					}
					break;
				default:
					throw new PluginException("Unsupported permission: "+permission,
						PluginCode.HttpForbidden);
			}
		}
		catch (PluginException ex)
		{
			throw ex;
		} // Yuk!
		catch (Exception ex)
		{
			throw new PluginException(
				"ROI collection permission check error. User= "+user.getUsername()+
					" Permission= "+permission,
				PluginCode.HttpInternalError, ex);
		}
	}

	private Security()
	{}
}
