INSERT INTO ui_parameter (id, category, name, type, value)
VALUES  (1, 'theme', 'primaryColor', 'STRING', '#26336B'),
        (2, 'theme', 'secondaryColor', 'STRING', '#26336B'),
        (3, 'theme', 'primaryTextColor', 'STRING', '#3c3c3d'),
        (4, 'theme', 'secondaryTextColor', 'STRING', '#3c3c3d'),
        (5, 'theme', 'appBackgroundColor', 'STRING', '#ffffff'),
        (6, 'theme', 'LinksTextColor', 'STRING', '#3c3c3d'),
        (7, 'theme', 'LinksColor', 'STRING', '#26336B'),
        (8, 'theme', 'ButtonColor', 'STRING', '#26336B'),
        (9, 'navbar', 'navbarLogoUrl', 'STRING', 'bbmri'),
        (10, 'navbar', 'navbarBackgroundColor', 'STRING', '#e7e7e7'),
        (11, 'navbar', 'navbarTextColor', 'STRING', '#3c3c3d'),
        (12, 'navbar', 'navbarActiveTextColor', 'STRING', '#e95713'),
        (13, 'navbar', 'navbarWelcomeTextColor', 'STRING', '#3c3c3d'),
        (14, 'navbar', 'navbarButtonOutlineColor', 'STRING', '#003674'),
        (15, 'footer', 'isFooterLeftSideIconVisible', 'BOOL', true),
        (16, 'footer', 'footerLeftSideIcon', 'STRING', 'bbmri'),
        (17, 'footer', 'footerLeftSideIconLink', 'STRING', 'https://www.bbmri-eric.eu/'),
        (18, 'footer', 'isFooterFollowUsVisible', 'BOOL', true),
        (19, 'footer', 'footerFollowUsLinkedin', 'STRING', 'https://www.linkedin.com/company/bbmri-eric'),
        (20, 'footer', 'footerFollowUsX', 'STRING', 'https://twitter.com/BBMRIERIC'),
        (21, 'footer', 'footerFollowUsPodcast', 'STRING', 'https://www.bbmri-eric.eu/bbmri-eric/bbmri-eric-podcast/'),
        (22, 'footer', 'isFooterGithubVisible', 'BOOL', true),
        (23, 'footer', 'footerGithubFrontendLink', 'STRING', 'https://github.com/BBMRI-ERIC/negotiator-v3-frontend'),
        (24, 'footer', 'footerGithubBackendLink', 'STRING', 'https://github.com/BBMRI-ERIC/negotiator'),
        (25, 'footer', 'isFooterSwaggerVisible', 'BOOL', true),
        (26, 'footer', 'footerSwaggerLink', 'STRING', '/api/swagger-ui/index.html'),
        (27, 'footer', 'footerSwaggerText', 'STRING', 'API'),
        (28, 'footer', 'isFooterStatusPageVisible', 'BOOL', true),
        (29, 'footer', 'footerStatusPageText', 'STRING', 'BBMRI-ERIC Status Page'),
        (30, 'footer', 'footerStatusPageLink', 'STRING', 'https://status.bbmri-eric.eu/'),
        (31, 'footer', 'isFooterWorkProgrammeVisible', 'BOOL', true),
        (32, 'footer', 'footerWorkProgrammeLink', 'STRING', 'https://www.bbmri-eric.eu/wp-content/uploads/BBMRI-ERIC_work-program_2022-2024_DIGITAL.pdf'),
        (33, 'footer', 'footerWorkProgrammeImageUrl', 'STRING', 'workProgramme'),
        (34, 'footer', 'footerWorkProgrammeText', 'STRING', 'Work Programme'),
        (35, 'footer', 'isFooterNewsletterVisible', 'BOOL', true),
        (36, 'footer', 'footerNewsletterLink', 'STRING', 'https://www.bbmri-eric.eu/news-event/'),
        (37, 'footer', 'footerNewsletterText', 'STRING', 'Subscribe To Our Newsletter'),
        (38, 'footer', 'isFooterPrivacyPolicyVisible', 'BOOL', true),
        (39, 'footer', 'footerPrivacyPolicyLink', 'STRING', 'https://www.bbmri-eric.eu/wp-content/uploads/AoM_10_8_Access-Policy_FINAL_EU.pdf'),
        (40, 'footer', 'footerPrivacyPolicyText', 'STRING', 'Privacy Policy'),
        (41, 'footer', 'isFooterCopyRightVisible', 'BOOL', true),
        (42, 'footer', 'footerCopyRightText', 'STRING', '© 2024 BBMRI-ERIC'),
        (43, 'footer', 'isFooterHelpLinkVisible', 'BOOL', true),
        (44, 'footer', 'footerHelpLink', 'STRING', 'mailto:negotiator@helpdesk.bbmri-eric.eu'),
        (45, 'footer', 'footerTextColor', 'STRING', '#3c3c3d'),
        (46, 'footer', 'footerNewsletterButtonColor', 'STRING', '#e7e7e7'),
        (47, 'login', 'loginLogoUrl', 'STRING', 'bbmri'),
        (48, 'login', 'loginNegotiatorTextColor', 'STRING', '#4d4d4f'),
        (49, 'login', 'loginTextColor', 'STRING', '#26336B'),
        (50, 'login', 'loginLinksTextColor', 'STRING', '#3c3c3d'),
        (51, 'login', 'loginLinksColor', 'STRING', '#26336B'),
        (52, 'login', 'logincardColor', 'STRING', '#ffffff'),
        (53, 'filtersSort', 'filtersSortButtonColor', 'STRING', '#003674'),
        (54, 'filtersSort', 'filtersSortDropdownTextColor', 'STRING', '#3c3c3d'),
        (55, 'filtersSort', 'filtersSortClearButtonColor', 'STRING', '#dc3545'),
        (56, 'negotiationList', 'searchResultsTextColor', 'STRING', '#3c3c3d'),
        (57, 'negotiationList', 'displayViewButtonColor', 'STRING', '#f37125'),
        (58, 'negotiationList', 'cardTextColor', 'STRING', '#3c3c3d'),
        (59, 'negotiationList', 'tableTextColor', 'STRING', '#3c3c3d'),
        (60, 'newRequestButton', 'isButtonVisible', 'BOOL', true),
        (61, 'newRequestButton', 'buttonText', 'STRING', 'New Request'),
        (63, 'newRequestButton', 'buttonColor', 'STRING', '#e95713'),
        (64, 'newRequestButton', 'buttonTextColor', 'STRING', '#ffffff'),
        (65, 'newRequestButton', 'buttonUrl', 'STRING', 'https://directory.bbmri-eric.eu'),
        (66, 'newRequestButton', 'modalTittle', 'STRING', 'New Request'),
        (67, 'newRequestButton', 'modalText', 'STRING', 'You will be redirected to our Metadata catalogue where you can select which collections you want to contact.');