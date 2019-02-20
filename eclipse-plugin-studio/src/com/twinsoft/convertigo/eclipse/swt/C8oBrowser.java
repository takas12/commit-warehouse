/*
 * Copyright (c) 2001-2019 Convertigo SA.
 * 
 * This program  is free software; you  can redistribute it and/or
 * Modify  it  under the  terms of the  GNU  Affero General Public
 * License  as published by  the Free Software Foundation;  either
 * version  3  of  the  License,  or  (at your option)  any  later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;  without even the implied warranty of
 * MERCHANTABILITY  or  FITNESS  FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */

package com.twinsoft.convertigo.eclipse.swt;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserException;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.twinsoft.convertigo.beans.core.Project;
import com.twinsoft.convertigo.engine.Engine;
import com.twinsoft.convertigo.engine.util.FileUtils;

public class C8oBrowser extends Composite {
	
	static {
		int port = 18082;
		BrowserPreferences.setChromiumSwitches("--remote-debugging-port=" + port);
	}
	
	private static Thread threadSwt = null;

	private BrowserView browserView;

	private void init(Composite parent, BrowserContext browserContext) {
		browserView = new BrowserView(new Browser(browserContext));
		Frame frame = SWT_AWT.new_Frame(this);
		frame.add(browserView);
		threadSwt = parent.getDisplay().getThread();
		parent.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
	}
	
	public C8oBrowser(Composite parent, int style) {
		super(parent, style | SWT.EMBEDDED | SWT.NO_BACKGROUND);
		init(parent, BrowserContext.defaultContext());
	}

	public C8oBrowser(Composite parent, int style, Project project) {
		super(parent, style | SWT.EMBEDDED | SWT.NO_BACKGROUND);
		boolean retry = false;
		do {
			File browserIdFile = new File(project.getDirPath() + "/_private/browser_id");
			String browserId = Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
			try {
				browserId = FileUtils.readFileToString(browserIdFile, "UTF-8");
			} catch (Exception e) {
				try {
					FileUtils.write(browserIdFile, browserId, "UTF-8");
				} catch (IOException e1) {
				}
			}
			File browserWorks = new File(Engine.USER_WORKSPACE_PATH + "/browser-works");
			browserWorks.mkdirs();
			BrowserContext browserContext = new BrowserContext(new BrowserContextParams(Engine.USER_WORKSPACE_PATH + "/browser-works/" + browserId));
			try {
				init(parent, browserContext);
			} catch (BrowserException e) {
				if (!retry) {
					browserIdFile.delete();
					retry = true;
				} else {
					throw e;
				}
			}
		} while (retry);
	}

	public C8oBrowser(Composite parent, int style, BrowserContext browserContext) {
		super(parent, style | SWT.EMBEDDED | SWT.NO_BACKGROUND);
		init(parent, browserContext);
	}
	
	@Override
	public void dispose() {
		run(() -> {
			getBrowser().dispose();			
		});
		super.dispose();
	}

	public BrowserView getBrowserView() {
		return browserView;
	}

	public Browser getBrowser() {
		return browserView.getBrowser();
	}
	
	public void setText(String html) {
		html = html.replace("<html>", "").replace("</html>", "");
		if (html.contains("$background$")) {
			org.eclipse.swt.graphics.Color bg = getBackground();
			String background = "rgb(" + bg.getRed() + ", " + bg.getGreen() + ", " + bg.getBlue() + ")";
			String foreground = bg.getRed() < 128 ? "white" : "black";
			String link = bg.getRed() < 128 ? "cyan" : "blue";
			html = html.replace("$background$", background).replace("$foreground$", foreground).replace("$link$", link);
		}
		getBrowser().getDocument().getDocumentElement().setInnerHTML(html);
	}
	
	public void reloadText() {
		Browser browser = getBrowser();
		setText(browser.getHTML());
	}

	public void setUrl(String url) {
		getBrowser().loadURL(url);
	}
		
	@Override
	public boolean setFocus() {
		C8oBrowser.run(() -> browserView.requestFocus());
		return super.setFocus();
	}

	public void addProgressListener(ProgressListener progressListener) {
		getBrowser().addLoadListener(new LoadListener() {
			
			@Override
			public void onStartLoadingFrame(StartLoadingEvent event) {
			}
			
			@Override
			public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
			}
			
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent event) {
				progressListener.completed(null);
			}
			
			@Override
			public void onFailLoadingFrame(FailLoadingEvent event) {
				
			}
			
			@Override
			public void onDocumentLoadedInMainFrame(LoadEvent event) {
			}
			
			@Override
			public void onDocumentLoadedInFrame(FrameLoadEvent event) {
				
			}
		});
	}
	
	public static void run(Runnable runnable) {
		if (threadSwt != null && threadSwt.equals(Thread.currentThread())) {
			Engine.execute(runnable);
		} else {
			runnable.run();
		}
	}
}
